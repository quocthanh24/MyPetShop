package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.UpdateMedicalRecordDetailRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMedicalRecordDetailService {

    Page<MRDResponse>  getLatestMedicalRecordDetailById(String id, Pageable pageable);
    MRDResponse updateMRD(UpdateMedicalRecordDetailRequest reqDTO, String id);
}
