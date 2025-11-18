package com.thanhluu.tlcn.DTO.request.Appointment;

import com.thanhluu.tlcn.Enum.AppointmentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentReq {
  @NotEmpty(message = "Customer phone number should not be empty")
  private String customerPhoneNumber;
  @NotEmpty(message = "Employee Id should not be empty")
  private String employeeId;
  @NotNull(message = "Appointment time should not be null")
  private LocalDateTime appointmentTime;
  private AppointmentStatus status;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
  private String description;
}
