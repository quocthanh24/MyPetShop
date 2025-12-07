package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.UpdateMedicalRecordDetailRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.Entity.MedicalRecordDetailEntity;
import com.thanhluu.tlcn.Entity.MedicalRecordEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.MedicalRecordMapper;
import com.thanhluu.tlcn.Repository.MedicalRecordDetailRepository;
import com.thanhluu.tlcn.Repository.MedicalRecordRepository;
import com.thanhluu.tlcn.Service.Employee.IMedicalRecordDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MedicalRecordDetailServiceImpl implements IMedicalRecordDetailService {

    @Autowired
    private MedicalRecordDetailRepository mrdRepository;

    @Autowired
    private MedicalRecordRepository mrRepository;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public Page<MRDResponse> getLatestMedicalRecordDetailById(String medicalRecordId, Pageable pageable) {
        UUID id = UUID.fromString(medicalRecordId);
        Page<MedicalRecordDetailEntity> mrdEntities =
          mrdRepository.findByMedicalRecord_IdOrderByUpdatedDateDesc(id, pageable);
        if (mrdEntities.isEmpty()) {
            log.info("Danh sách chi tiết thông tin bệnh án bị rỗng");
        }
        return mrdEntities.map(medicalRecordMapper::toDTO_MRD);
    }

    @Override
    public MRDResponse updateMRD(UpdateMedicalRecordDetailRequest reqDTO, String id) {

        MedicalRecordEntity medicalRecord = mrRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEDICAL_RECORD_NOT_FOUND));
        MedicalRecordDetailEntity mrdEntity = medicalRecordMapper.toEntity_Update(reqDTO);
        mrdEntity.setMedicalRecord(medicalRecord);
        MedicalRecordDetailEntity savedEntity = mrdRepository.save(mrdEntity);

        return medicalRecordMapper.toDTO_MRD(savedEntity);
    }


}
