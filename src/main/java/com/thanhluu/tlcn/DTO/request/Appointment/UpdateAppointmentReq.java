package com.thanhluu.tlcn.DTO.request.Appointment;

import com.thanhluu.tlcn.Enum.AppointmentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppointmentReq {
  @NotEmpty(message = "Employee Id should not me empty")
  private String employeeId;
  private LocalDateTime appointmentTime;
  private AppointmentStatus status;
  private String description;
}

