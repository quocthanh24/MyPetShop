package com.thanhluu.tlcn.Controller.Employee.Appointment;

import com.thanhluu.tlcn.DTO.request.Appointment.CreateAppointmentReq;
import com.thanhluu.tlcn.DTO.request.Appointment.UpdateAppointmentReq;
import com.thanhluu.tlcn.DTO.response.Appointment.AppointmentResp;
import com.thanhluu.tlcn.Service.Employee.IAppointmentService;
import com.thanhluu.tlcn.Service.Employee.Impl.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees/appointments")
public class AppointmentController {
  @Autowired
  private IAppointmentService appointmentService;


  @GetMapping
  public ResponseEntity<Page<AppointmentResp>> findAllAppointments(Pageable pageable) {
    return new ResponseEntity<>(appointmentService.getAppointments(pageable), HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<AppointmentResp> createAppointment(@RequestBody @Validated CreateAppointmentReq request) {
    return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
  }

  @PatchMapping("/update/{id}")
  public ResponseEntity<AppointmentResp> updateAppointment(
    @PathVariable String id,
    @RequestBody @Validated UpdateAppointmentReq request) {
    return new ResponseEntity<>(appointmentService.updateAppointment(id ,request), HttpStatus.OK);
  }
}
