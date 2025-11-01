package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.response.User.UserResponse;
import com.thanhluu.tlcn.DTO.response.User.User_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.User.User_PromotedResponse;
import com.thanhluu.tlcn.DTO.response.User.User_UnpromotedResponse;
import com.thanhluu.tlcn.Entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserRequest dto);

    UserResponse toDTO(UserEntity userEntity);

    User_PromotedResponse toDTO_Promoted(UserEntity userEntity);

    // method mapper hỗ trợ cho mapper từ list -> list
    User_UnpromotedResponse toDTO_Unpromoted(UserEntity userEntity);

    List<User_UnpromotedResponse> toListDTO(List<UserEntity> entites);

    // Phần liên quan đến bệnh án
    User_MedicalRecordResponse toDTO_MedicalRecord(UserEntity userEntity);
}
