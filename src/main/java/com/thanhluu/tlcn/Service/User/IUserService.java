package com.thanhluu.tlcn.Service.User;
import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.response.User.UserResponse;
import com.thanhluu.tlcn.Entity.UserEntity;


public interface IUserService {

    UserResponse register(UserRequest dto);

    UserEntity login(String id);

    UserEntity save(UserEntity userEntity);

    UserEntity findById(String id);

}
