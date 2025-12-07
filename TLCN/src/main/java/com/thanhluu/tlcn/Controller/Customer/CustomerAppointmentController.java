package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.Service.Employee.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/appointments")
public class CustomerAppointmentController {
  @Autowired
  private IAppointmentService appointmentService;

  @GetMapping
  public ResponseEntity<?> getListAppointments(@RequestParam String userId, Pageable pageable) {
    return new ResponseEntity<>(appointmentService.getAppointmentsByCustomer(userId, pageable), HttpStatus.OK);
  }
}
