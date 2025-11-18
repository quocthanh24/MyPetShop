package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.UpdateMedicalRecordDetailRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.GetAll_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.ViewAllDetailByPhoneNumberResp;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.User.User_MedicalRecordResponse;
import com.thanhluu.tlcn.Entity.MedicalRecordDetailEntity;
import com.thanhluu.tlcn.Entity.MedicalRecordEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PetMapper.class})
public interface MedicalRecordMapper {

    @Mapping(source = "medicalRecordEntity.createdDate", target = "createdDate")
    @Mapping(source = "medicalRecordEntity.employee.phoneNumber", target = "employeePhoneNumber")
    @Mapping(source = "medicalRecordEntity.id", target = "medicalRecordId")
    MedicalRecordResponse toDTO(MedicalRecordEntity medicalRecordEntity,
                                Pet_MedicalRecordResponse pet ,
                                User_MedicalRecordResponse owner,
                                MRDResponse recordDetails);

    MedicalRecordEntity toEntity(MedicalRecordRequest medicalRecordRequestDTO);

    @Mapping(source = "medicalRecordEntity.employee.fullName" , target = "createdBy")
    @Mapping(source = "medicalRecordEntity.pet" , target = "pet")
    GetAll_MedicalRecordResponse toDTO_GetAll(MedicalRecordEntity medicalRecordEntity);

    // Phần cho Medical Record Detail

    @Mapping(source = "reqDTO.recordDetails.healthCondition" , target = "healthCondition")
    @Mapping(source = "reqDTO.recordDetails.medicalHistory" , target = "medicalHistory")
    @Mapping(source = "reqDTO.recordDetails.updatedDate" , target = "updatedDate")
    @Mapping(source = "reqDTO.recordDetails.temperature" , target = "temperature")
    @Mapping(source = "reqDTO.recordDetails.vaccines" , target = "vaccines")
    @Mapping(source = "reqDTO.recordDetails.diagnosisResult" , target = "diagnosisResult")
    MedicalRecordDetailEntity toEntity_MRD(MedicalRecordRequest reqDTO);

    MRDResponse toDTO_MRD(MedicalRecordDetailEntity medicalRecordDetailEntity);

    @Mapping(source = "id", target = "medicalRecordId")
    @Mapping(source = "employee.fullName", target = "createdBy")
    @Mapping(source = "medicalRecordDetailEntities", target = "medicalRecordDetails")
    ViewAllDetailByPhoneNumberResp toDTO_ViewAllDetails(MedicalRecordEntity medicalRecordEntity);
    MedicalRecordDetailEntity toEntity_Update(UpdateMedicalRecordDetailRequest reqDTO);


}
