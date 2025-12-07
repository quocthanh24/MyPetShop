package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Pet.PetCreateRequest;
import com.thanhluu.tlcn.DTO.request.Pet.Pet_MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.response.Pet.PetCreateResponse;
import com.thanhluu.tlcn.DTO.response.Pet.PetFindByCodeResponse;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MRDResponse;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import com.thanhluu.tlcn.Entity.PetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface PetMapper {

    PetEntity toEntity(PetCreateRequest petRequestDTOCreate);

    PetFindByCodeResponse toDTO_FindByCode(PetEntity petEntity);

    PetCreateResponse toDTO_Create(PetEntity petEntity);

    @Mappings({})
    Pet_MedicalRecordResponse toDTO_MedicalRecord(PetEntity petEntity);

    PetEntity toEntity_MedicalRecord(Pet_MedicalRecordRequest petRequestDTOMedicalRecord);

    @Mappings({})
    Pet_MRDResponse toDTO_MedicalRecordDetail(PetEntity petEntity);

}
