package com.thanhluu.tlcn.Controller.Employee.MedicalRecord;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.request.MedicalRecord.PhonenumberRequest;
import com.thanhluu.tlcn.Service.Employee.IMedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/med-records")
public class MedicalRecordController {
    @Autowired
    private IMedicalRecordService medicalRecordService;

    @PostMapping("/get-by-phone-number")
    public ResponseEntity<?> getAllMedicalRecordByCustomer(@RequestBody @Validated PhonenumberRequest ownerPhonenumber) {
        return new ResponseEntity<>(medicalRecordService.getAllMedicalRecordsByCustomerPhonenumber(ownerPhonenumber.getPhoneNumber()),HttpStatus.OK);
    }


    @PostMapping("/create-medrecord")
    public ResponseEntity<?> createMedicalRecord(@RequestBody @Validated MedicalRecordRequest reqDTO) {
        return new ResponseEntity<>(medicalRecordService.createMedicalRecordEntity(reqDTO), HttpStatus.CREATED);
    }

}
