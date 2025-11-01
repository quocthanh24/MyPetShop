package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.GetAll_MedicalRecordResponse;

import java.util.List;

public interface IMedicalRecordService {

    MedicalRecordResponse createMedicalRecordEntity(MedicalRecordRequest medicalRecordRequest);

    List<GetAll_MedicalRecordResponse> getAllMedicalRecordsByCustomerPhonenumber(String phonenumber);

}
