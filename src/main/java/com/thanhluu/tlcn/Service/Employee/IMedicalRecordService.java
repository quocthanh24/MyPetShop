package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.GetAll_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.ViewAllDetailByPhoneNumberResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMedicalRecordService {

    MedicalRecordResponse createMedicalRecordEntity(MedicalRecordRequest medicalRecordRequest);

    Page<ViewAllDetailByPhoneNumberResp> getAllMedicalRecordsByCustomerPhoneNumber(String phoneNumber, Pageable pageable);

}
