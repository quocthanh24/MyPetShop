package com.thanhluu.tlcn.Util;

import com.thanhluu.tlcn.Entity.AppointmentEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class JavaMailUtil {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private final JavaMailSender mailSender;
  private final TaskScheduler taskScheduler;
  private final Map<UUID, ScheduledFuture<?>> reminderTasks = new ConcurrentHashMap<>();

  @Value("${app.mail.from}")
  private String fromAddress;

  public JavaMailUtil(JavaMailSender mailSender) {
    this.mailSender = mailSender;
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(2);
    scheduler.setThreadNamePrefix("appointment-reminder-");
    scheduler.initialize();
    this.taskScheduler = scheduler;
  }

  public void handleAppointmentCreated(AppointmentEntity appointment) {
    AppointmentSnapshot snapshot = createSnapshot(appointment);
    if (snapshot.isDeliverable()) {
      log.warn("Skip sending creation email for appointment {} due to missing customer email", snapshot.id());
      return;
    }
    sendEmail(snapshot.customerEmail(), "Xác nhận lịch hẹn", buildCreatedContent(snapshot));
    scheduleReminder(snapshot);
  }

  public void handleAppointmentUpdated(AppointmentEntity appointment) {
    AppointmentSnapshot snapshot = createSnapshot(appointment);
    if (snapshot.isDeliverable()) {
      log.warn("Skip sending update email for appointment {} due to missing customer email", snapshot.id());
      return;
    }
    sendEmail(snapshot.customerEmail(), "Cập nhật lịch hẹn", buildUpdatedContent(snapshot));
    scheduleReminder(snapshot);
  }

  private void scheduleReminder(AppointmentSnapshot snapshot) {
    if (snapshot.appointmentTime() == null) {
      log.warn("Skip scheduling reminder for appointment {} because appointment time is null", snapshot.id());
      cancelReminder(snapshot.id());
      return;
    }

    cancelReminder(snapshot.id());

    LocalDateTime reminderTime = snapshot.appointmentTime().minusHours(1);
    if (reminderTime.isBefore(LocalDateTime.now())) {
      log.info("Reminder time for appointment {} already passed, sending immediately", snapshot.id());
      sendReminderEmail(snapshot);
      return;
    }

    Instant reminderInstant = reminderTime.atZone(ZoneId.systemDefault()).toInstant();
    ScheduledFuture<?> future = taskScheduler.schedule(() -> sendReminderEmail(snapshot), Date.from(reminderInstant));
    reminderTasks.put(snapshot.id(), future);
    log.info("Scheduled reminder email for appointment {} at {}", snapshot.id(), reminderTime);
  }

  private void sendReminderEmail(AppointmentSnapshot snapshot) {
    if (snapshot.isDeliverable()) {
      log.warn("Skip sending reminder email for appointment {} due to missing customer email", snapshot.id());
      return;
    }
    sendEmail(snapshot.customerEmail(), "Nhắc lịch hẹn", buildReminderContent(snapshot));
  }

  public void sendOrderCodeEmail(String email, String orderCode, String customerName) {
    if (email == null || email.isBlank()) {
      log.warn("Skip sending order code email due to missing recipient email");
      return;
    }

    String subject = "Mã vận đơn của đơn hàng của bạn";
    String content = buildOrderTracking(customerName, orderCode);

    sendEmail(email, subject, content);
  }

  private void sendEmail(String recipient, String subject, String content) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
      helper.setFrom(fromAddress);
      helper.setTo(recipient);
      helper.setSubject(subject);
      helper.setText(content, true);
      mailSender.send(message);
      log.info("Sent email '{}' to {}", subject, recipient);
    } catch (MailException | MessagingException ex) {
      log.error("Failed to send email '{}' to {}", subject, recipient, ex);
    }
  }

  private void cancelReminder(UUID appointmentId) {
    ScheduledFuture<?> existingTask = reminderTasks.remove(appointmentId);
    if (existingTask != null) {
      existingTask.cancel(false);
      log.debug("Cancelled existing reminder for appointment {}", appointmentId);
    }
  }

  private AppointmentSnapshot createSnapshot(AppointmentEntity appointment) {
    UUID id = appointment.getId();
    UserEntity customer = appointment.getCustomer();
    UserEntity employee = appointment.getEmployee();

    String customerEmail = Optional.ofNullable(customer).map(UserEntity::getGmail).orElse(null);
    String customerName = Optional.ofNullable(customer).map(UserEntity::getFullName).orElse("Khách hàng");
    String employeeName = Optional.ofNullable(employee).map(UserEntity::getFullName).orElse("Nhân viên thú y");

    return new AppointmentSnapshot(
      id,
      customerEmail,
      customerName,
      employeeName,
      appointment.getAppointmentTime(),
      appointment.getStatus() == null ? "" : appointment.getStatus().name(),
      appointment.getDescription()
    );
  }

  private String buildCreatedContent(AppointmentSnapshot snapshot) {
    return "Xin chào " +
      snapshot.customerName() +
      ",<br/><br/>" +
      "Lịch hẹn của bạn đã được tạo thành công với các thông tin sau:<br/>" +
      "• Thời gian: " +
      formatDate(snapshot.appointmentTime()) +
      "<br/>" +
      "• Nhân viên phụ trách: " +
      snapshot.employeeName() +
      "<br/>" +
      buildDescriptionLine(snapshot.description()) +
      "<br/><br/>Xin cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!";
  }

  private String buildUpdatedContent(AppointmentSnapshot snapshot) {
    return "Xin chào " +
      snapshot.customerName() +
      ",<br/><br/>" +
      "Lịch hẹn của bạn đã được cập nhật. Thông tin mới nhất:<br/>" +
      "• Thời gian: " +
      formatDate(snapshot.appointmentTime()) +
      "<br/>" +
      "• Nhân viên phụ trách: " +
      snapshot.employeeName() +
      "<br/>" +
      buildDescriptionLine(snapshot.description()) +
      "<br/><br/>Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.";
  }

  private String buildReminderContent(AppointmentSnapshot snapshot) {
    return "Xin chào " +
      snapshot.customerName() +
      ",<br/><br/>" +
      "Đây là email nhắc nhở lịch hẹn sẽ diễn ra sau 1 giờ.<br/>" +
      "• Thời gian: " +
      formatDate(snapshot.appointmentTime()) +
      "<br/>" +
      "• Nhân viên phụ trách: " +
      snapshot.employeeName() +
      "<br/>" +
      buildDescriptionLine(snapshot.description()) +
      "<br/><br/>Hẹn gặp bạn tại phòng khám!";
  }

  private String buildDescriptionLine(String description) {
    if (Objects.isNull(description) || description.isBlank()) {
      return "• Ghi chú: (Không có)";
    }
    return "• Ghi chú: " + description;
  }

  private String buildOrderTracking(String customerName, String orderCode) {
    return "Xin chào " + customerName + ",<br/><br/>" +
      "Đơn hàng của bạn đã được tạo thành công.<br/><br/>" +
      "Mã vận đơn của bạn là: <b>" + orderCode + "</b><br/><br/>" +
      "Bạn có thể theo dõi trạng thái đơn hàng tại đường dẫn sau:<br/>" +
      "<a href='https://tracking.ghn.dev/?order_code=" + orderCode + "'>" +
      "Tra cứu đơn hàng tại GHN" +
      "</a><br/><br/>" +
      "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!";
  }


  private String formatDate(LocalDateTime time) {
    if (time == null) {
      return "(chưa xác định)";
    }
    return DATE_TIME_FORMATTER.format(time);
  }

  /**
   * Gửi OTP code qua email cho việc đăng ký tài khoản
   * @param email Email người dùng
   * @param otpCode Mã OTP cần gửi
   * @param recipientName Tên người nhận (có thể là null)
   */
  public void sendOtpEmail(String email, String otpCode) {
    if (email == null || email.isBlank()) {
      log.warn("Skip sending OTP email due to missing recipient email");
      return;
    }

    String name = "Bạn";
    String subject = "Mã OTP đăng ký tài khoản";
    String content = buildOtpEmailContent(name, otpCode);
    
    sendEmail(email, subject, content);
  }

  public void sendOTPResetPassword(String email, String otpCode) {
    if (email == null || email.isBlank()) {
      log.warn("Skip sending OTP reset email due to missing recipient email");
      return;
    }

    String name = "Bạn";
    String subject = "Mã OTP reset mật khẩu";
    String content = buildOTPResetPasswordContent(name , otpCode);

    sendEmail(email, subject, content);
  }

  private String buildOTPResetPasswordContent(String name, String otpCode) {
    return "Xin chào " + name + ",<br/><br/>" +
      "Mã OTP của bạn để reset mật khẩu là: <br/><br/>" +
      "<h2 style='color: #4CAF50; font-size: 32px; letter-spacing: 5px; text-align: center;'>" + otpCode + "</h2><br/>" +
      "Mã OTP này sẽ hết hạn sau 5 phút.<br/>" +
      "Vui lòng không chia sẻ mã này với bất kỳ ai.<br/><br/>" +
      "Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.<br/><br/>" +
      "Xin cảm ơn!";
  }

  private String buildOtpEmailContent(String recipientName, String otpCode) {
    return "Xin chào " + recipientName + ",<br/><br/>" +
      "Mã OTP của bạn để đăng ký tài khoản là: <br/><br/>" +
      "<h2 style='color: #4CAF50; font-size: 32px; letter-spacing: 5px; text-align: center;'>" + otpCode + "</h2><br/>" +
      "Mã OTP này sẽ hết hạn sau 5 phút.<br/>" +
      "Vui lòng không chia sẻ mã này với bất kỳ ai.<br/><br/>" +
      "Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.<br/><br/>" +
      "Xin cảm ơn!";
  }

  @PreDestroy
  public void shutdownScheduler() {
    if (taskScheduler instanceof ThreadPoolTaskScheduler threadPoolTaskScheduler) {
      threadPoolTaskScheduler.shutdown();
    }
  }

  private record AppointmentSnapshot(
    UUID id,
    String customerEmail,
    String customerName,
    String employeeName,
    LocalDateTime appointmentTime,
    String status,
    String description
  ) {
    boolean isDeliverable() {
      return id == null || customerEmail == null || customerEmail.isBlank();
    }
  }
}
