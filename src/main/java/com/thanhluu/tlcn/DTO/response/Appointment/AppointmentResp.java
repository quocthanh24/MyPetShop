package com.thanhluu.tlcn.DTO.response.Appointment;

import com.thanhluu.tlcn.DTO.response.User.Customer.CustomerInfoResp;
import com.thanhluu.tlcn.Enum.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResp {
  private UUID id;
  private LocalDateTime appointmentTime;
  private AppointmentStatus status;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private String description;
  private String acceptedBy;
  private CustomerInfoResp customer;
}
