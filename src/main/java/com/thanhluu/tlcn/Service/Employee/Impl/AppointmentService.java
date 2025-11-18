package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.Appointment.CreateAppointmentReq;
import com.thanhluu.tlcn.DTO.request.Appointment.UpdateAppointmentReq;
import com.thanhluu.tlcn.DTO.response.Appointment.AppointmentResp;
import com.thanhluu.tlcn.Entity.AppointmentEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.AppointmentStatus;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.AppointmentMapper;
import com.thanhluu.tlcn.Repository.AppointmentRepository;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Employee.IAppointmentService;
import com.thanhluu.tlcn.Util.JavaMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@Slf4j
public class AppointmentService implements IAppointmentService {
  @Autowired
  private AppointmentRepository appointmentRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AppointmentMapper appointmentMapper;
  @Autowired
  private JavaMailUtil javaMailUtil;


  @Override
  public Page<AppointmentResp> getAppointments(Pageable pageable) {
    return appointmentRepository.findAll(pageable).map(appointmentMapper::toDTO);
  }

  @Override
  public AppointmentResp bookAppointment(CreateAppointmentReq request) {
    UserEntity customer = userRepository.findByPhoneNumber(request.getCustomerPhoneNumber())
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    UserEntity employee = userRepository.findById(UUID.fromString(request.getEmployeeId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    AppointmentEntity appointmentEntity = appointmentMapper.toEntity(request);
    appointmentEntity.setCreatedDate(now());
    appointmentEntity.setStatus(AppointmentStatus.SCHEDULED);
    appointmentEntity.setCustomer(customer);
    appointmentEntity.setEmployee(employee);
    appointmentEntity = appointmentRepository.save(appointmentEntity);
    javaMailUtil.handleAppointmentCreated(appointmentEntity);
    return appointmentMapper.toDTO(appointmentEntity);
  }

  @Override
  public AppointmentResp updateAppointment(String id, UpdateAppointmentReq request) {
    AppointmentEntity appointmentEntity = appointmentRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new BadRequestException(ErrorCode.APPOINTMENT_NOT_FOUND));
    UserEntity employee = userRepository.findById(UUID.fromString(request.getEmployeeId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    appointmentMapper.updateEntityFromRequest(request, appointmentEntity);
    appointmentEntity.setModifiedDate(now());
    appointmentEntity.setEmployee(employee);
    log.info("Appointment time is {}" , appointmentEntity.getAppointmentTime());
    appointmentEntity = appointmentRepository.save(appointmentEntity);
    javaMailUtil.handleAppointmentUpdated(appointmentEntity);
    return appointmentMapper.toDTO(appointmentEntity);
  }
}
