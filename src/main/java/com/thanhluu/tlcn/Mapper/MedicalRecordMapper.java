package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.MedicalRecordDetailUpdateRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.GetAll_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.User.User_MedicalRecordResponse;
import com.thanhluu.tlcn.Entity.MedicalRecordDetailEnitty;
import com.thanhluu.tlcn.Entity.MedicalRecordEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PetMapper.class})
public interface MedicalRecordMapper {

    @Mapping(source = "medicalRecordEntity.createdDate", target = "createdDate")
    @Mapping(source = "medicalRecordEntity.employee.phoneNumber", target = "employeePhonenumber")
    MedicalRecordResponse toDTO(MedicalRecordEntity medicalRecordEntity,
                                Pet_MedicalRecordResponse pet ,
                                User_MedicalRecordResponse owner,
                                MRDResponse recordDetails);

    MedicalRecordEntity toEntity(MedicalRecordRequest medicalRecordRequestDTO);

    @Mapping(source = "medicalRecordEntity.employee.fullName" , target = "createdBy")
    @Mapping(source = "medicalRecordEntity.pet" , target = "pet")
    GetAll_MedicalRecordResponse toDTO_GetAll(MedicalRecordEntity medicalRecordEntity);

    List<GetAll_MedicalRecordResponse> toListDTO_GetAll (List<MedicalRecordEntity> entities);

    // Phần cho Medical Record Detail

    @Mapping(source = "reqDTO.recordDetails.healthCondition" , target = "healthCondition")
    @Mapping(source = "reqDTO.recordDetails.medicalHistory" , target = "medicalHistory")
    @Mapping(source = "reqDTO.recordDetails.updatedDate" , target = "updatedDate")
    @Mapping(source = "reqDTO.recordDetails.temperature" , target = "temperature")
    @Mapping(source = "reqDTO.recordDetails.vaccines" , target = "vaccines")
    @Mapping(source = "reqDTO.recordDetails.diagnosisResult" , target = "diagnosisResult")
    MedicalRecordDetailEnitty toEntity_MRD(MedicalRecordRequest reqDTO);

    MRDResponse toDTO_MRD(MedicalRecordDetailEnitty medicalRecordDetailEnitty);

    List<MRDResponse> toListDTO_MRD(List<MedicalRecordDetailEnitty> medicalRecordDetailEnitties);

    MedicalRecordDetailEnitty toEntity_Update(MedicalRecordDetailUpdateRequest reqDTO);


}
