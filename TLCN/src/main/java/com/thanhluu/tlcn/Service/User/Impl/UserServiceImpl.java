package com.thanhluu.tlcn.Service.User.Impl;


import com.thanhluu.tlcn.DTO.request.User.RequestOtpRequest;
import com.thanhluu.tlcn.DTO.request.User.ResetPasswordRequest;
import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.request.User.VerifyOtpRegisterRequest;
import com.thanhluu.tlcn.DTO.response.User.UserResponse;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Enum.Role;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.UserMapper;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.OTPService;
import com.thanhluu.tlcn.Service.User.IUserService;
import com.thanhluu.tlcn.Util.JavaMailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OTPService otpService;
    private final JavaMailUtil javaMailUtil;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByGmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // Convert UserInfo to UserDetails (UserInfoDetails)
        return new User(userEntity.getGmail(),
                userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())));
    }


    @Override
    public UserEntity login(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity findById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    }

    @Override
    public UserEntity findByGmail(String email) {
        return userRepository.findByGmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByGmail(String email) {
        return userRepository.findByGmail(email).isPresent();
    }

    @Override
    public void sendOTPForRegister(RequestOtpRequest request) {
        // Generate OTP
        String otpCode = otpService.generateOtp();

        // Lưu OTP
        otpService.saveOtp(request.getGmail(), otpCode);

        // Gửi OTP qua email
        javaMailUtil.sendOtpEmail(request.getGmail(), otpCode);
    }

    @Override
    public void sendOTPForResetPassword(RequestOtpRequest request) {
        UserEntity user = userRepository.findByGmail(request.getGmail())
          .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        // Generate OTP
        String otpCode = otpService.generateOtp();

        // Lưu OTP
        otpService.saveOtp(request.getGmail(), otpCode);

        // Gửi OTP qua email
        javaMailUtil.sendOTPResetPassword(request.getGmail(), otpCode);
    }

    @Override
    public UserResponse resetPassword(ResetPasswordRequest request) {

        if (!request.getResetPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
        }

        UserEntity user = userRepository.findByGmail(request.getGmail())
          .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
        user.setPassword(passwordEncoder.encode(request.getResetPassword()));
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    @Override
    public UserResponse registerWithOtp(VerifyOtpRegisterRequest dto) {
        // Kiểm tra email đã tồn tại chưa
        if (existsByGmail(dto.getGmail())) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Verify OTP
        if (!otpService.verifyOtp(dto.getGmail(), dto.getOtpCode())) {
            throw new BadRequestException(ErrorCode.INVALID_OTP);
        }

        UserEntity userEntity = userMapper.toEntity(dto);
        userEntity.setRole(Role.CUSTOMER);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
    }


}
