package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.Appointment.CreateAppointmentReq;
import com.thanhluu.tlcn.DTO.request.Appointment.UpdateAppointmentReq;
import com.thanhluu.tlcn.DTO.response.Appointment.AppointmentResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAppointmentService {
  Page<AppointmentResp> getAppointments(Pageable pageable);

  AppointmentResp bookAppointment(CreateAppointmentReq request);

  AppointmentResp updateAppointment(String id, UpdateAppointmentReq request);
}
