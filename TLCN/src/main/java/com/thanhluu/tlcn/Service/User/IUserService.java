package com.thanhluu.tlcn.Service.User;
import com.thanhluu.tlcn.DTO.request.User.RequestOtpRequest;
import com.thanhluu.tlcn.DTO.request.User.ResetPasswordRequest;
import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.request.User.VerifyOtpRegisterRequest;
import com.thanhluu.tlcn.DTO.response.User.UserResponse;
import com.thanhluu.tlcn.Entity.UserEntity;


public interface IUserService {

    UserResponse registerWithOtp(VerifyOtpRegisterRequest dto);
    UserEntity login(String id);
    UserEntity save(UserEntity userEntity);
    UserEntity findById(String id);
    UserEntity findByGmail(String email);
    boolean existsByGmail(String email);
    void sendOTPForRegister(RequestOtpRequest request);
    void sendOTPForResetPassword(RequestOtpRequest request);
    UserResponse resetPassword(ResetPasswordRequest request);

}
