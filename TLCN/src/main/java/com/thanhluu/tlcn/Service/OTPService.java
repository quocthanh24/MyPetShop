package com.thanhluu.tlcn.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OTPService {

  private static final int OTP_LENGTH = 6;
  private static final int OTP_EXPIRY_MINUTES = 5;
  private static final SecureRandom random = new SecureRandom();

  private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

  /**
   * Tạo mã OTP ngẫu nhiên
   * @return Mã OTP 6 chữ số
   */
  public String generateOtp() {
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }

  /**
   * Lưu trữ OTP kèm thời gian hết hạn
   * @param email Email người dùng
   * @param otpCode Mã OTP
   * @return Mã OTP đã lưu
   */
  public String saveOtp(String email, String otpCode) {
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
    otpStorage.put(email, new OtpData(otpCode, expiryTime));
    log.info("OTP saved for email: {}, expires at: {}", email, expiryTime);
    return otpCode;
  }

  /**
   * Verify OTP code
   * @param email Email người dùng
   * @param otpCode Mã OTP cần verify
   * @return true nếu OTP hợp lệ và chưa hết hạn, false nếu không
   */
  public boolean verifyOtp(String email, String otpCode) {
    OtpData otpData = otpStorage.get(email);
    
    if (otpData == null) {
      log.warn("No OTP found for email: {}", email);
      return false;
    }

    if (LocalDateTime.now().isAfter(otpData.expiryTime())) {
      log.warn("OTP expired for email: {}, expiry time: {}", email, otpData.expiryTime());
      otpStorage.remove(email);
      return false;
    }

    if (!otpData.otpCode().equals(otpCode)) {
      log.warn("Invalid OTP for email: {}", email);
      return false;
    }

    // Xóa OTP sau khi verify thành công
    otpStorage.remove(email);
    log.info("OTP verified successfully for email: {}", email);
    return true;
  }

  /**
   * Xóa OTP đã hết hạn
   * @param email Email người dùng
   */
  public void removeOtp(String email) {
    otpStorage.remove(email);
    log.debug("OTP removed for email: {}", email);
  }

  /**
   * Kiểm tra xem email đã có OTP chưa
   * @param email Email người dùng
   * @return true nếu đã có OTP, false nếu chưa
   */
  public boolean hasOtp(String email) {
    return otpStorage.containsKey(email);
  }

  /**
   * Lấy thời gian hết hạn của OTP
   * @param email Email người dùng
   * @return LocalDateTime hoặc null nếu không tìm thấy
   */
  public LocalDateTime getOtpExpiryTime(String email) {
    OtpData otpData = otpStorage.get(email);
    return otpData != null ? otpData.expiryTime() : null;
  }

  /**
   * Record để lưu trữ OTP data
   */
  private record OtpData(String otpCode, LocalDateTime expiryTime) {
  }

}

