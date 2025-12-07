package com.thanhluu.tlcn.Controller;

import com.thanhluu.tlcn.DTO.request.User.*;
import com.thanhluu.tlcn.DTO.response.ApiResponse;
import com.thanhluu.tlcn.DTO.response.Authen.AuthenResponse;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Service.OTPService;
import com.thanhluu.tlcn.Service.User.IUserService;
import com.thanhluu.tlcn.Util.JavaMailUtil;
import com.thanhluu.tlcn.Util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccessController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OTPService otpService;

    @Autowired
    private JavaMailUtil javaMailUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest userRequestDTOLogin) {

        // Áp dụng spring security để sinh token
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestDTOLogin.getGmail(), userRequestDTOLogin.getPassword()));

            if (authentication.isAuthenticated()) {

                UserEntity user = userService.findByGmail(userRequestDTOLogin.getGmail());
                String userId = user.getId().toString();
                String accessToken = jwtUtil.generateToken(userRequestDTOLogin.getGmail(), userId);
                String refreshToken = jwtUtil.generateRefreshToken(userRequestDTOLogin.getGmail(), userId);

                return new ResponseEntity<>(new AuthenResponse(userId, accessToken, refreshToken, user.getRole()), HttpStatus.OK);

            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.USER_NOT_FOUND);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid VerifyOTPRequest request) {

        if (otpService.verifyOtp(request.getGmail(), request.getOtp())) {
            return ResponseEntity.ok(
              ApiResponse.builder()
                .status(200)
                .message("OTP hợp lệ")
                .data(null)
                .build()
            );
        }
        return ResponseEntity.badRequest().body(
          ApiResponse.builder()
            .status(400)
            .message("OTP không hợp lệ")
            .data(null)
            .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return new ResponseEntity<>(userService.resetPassword(request), HttpStatus.OK);
    }

    @PostMapping("/request-otp/reset-password")
    public ResponseEntity<?> requestOTPForPassword(@RequestBody @Valid RequestOtpRequest request) {
        userService.sendOTPForResetPassword(request);
        return ResponseEntity.ok(
          ApiResponse.builder()
            .status(200)
            .message("OTP đã được gửi đến " + request.getGmail())
            .data(null)
            .build()
        );
    }



    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody @Valid RequestOtpRequest request) {
       userService.sendOTPForRegister(request);
        return ResponseEntity.ok(
          ApiResponse.builder()
            .status(200)
            .message("OTP đã được gửi đến " + request.getGmail())
            .data(null)
            .build()
        );
    }

    @PostMapping("/register-with-otp")
    public ResponseEntity<?> registerWithOtp(@RequestBody @Validated VerifyOtpRegisterRequest dto) {
        return new ResponseEntity<>(userService.registerWithOtp(dto), HttpStatus.CREATED);
    }
}
