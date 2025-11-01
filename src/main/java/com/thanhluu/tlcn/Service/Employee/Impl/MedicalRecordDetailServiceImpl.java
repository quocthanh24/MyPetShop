package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.MedicalRecordDetailUpdateRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.Entity.MedicalRecordDetailEnitty;
import com.thanhluu.tlcn.Entity.MedicalRecordEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.MedicalRecordMapper;
import com.thanhluu.tlcn.Repository.MedicalRecordDetailRepository;
import com.thanhluu.tlcn.Repository.MedicalRecordRepository;
import com.thanhluu.tlcn.Service.Employee.IMedicalRecordDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MedicalRecordDetailServiceImpl implements IMedicalRecordDetailService {

    @Autowired
    private MedicalRecordDetailRepository mrdRepository;

    @Autowired
    private MedicalRecordRepository mrRepository;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public Page<MRDResponse> getLatestMedicalRecordDetailById(String id, Pageable pageable) {

            Page<MedicalRecordDetailEnitty> mrdEntities = mrdRepository.findByMedicalRecord_IdOrderByUpdatedDateDesc(UUID.fromString(id), pageable);

        // Convert sang List DTO rồi trả về List DTO
        return mrdEntities.map(medicalRecordMapper::toDTO_MRD);

    }

    @Override
    public MRDResponse updateMRD(MedicalRecordDetailUpdateRequest reqDTO, String id) {

        MedicalRecordEntity medicalRecord = mrRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEDICAL_RECORD_NOT_FOUND));
        MedicalRecordDetailEnitty mrdEntity = medicalRecordMapper.toEntity_Update(reqDTO);
        mrdEntity.setMedicalRecord(medicalRecord);
        MedicalRecordDetailEnitty savedEntity = mrdRepository.save(mrdEntity);

        return medicalRecordMapper.toDTO_MRD(savedEntity);
    }


}
