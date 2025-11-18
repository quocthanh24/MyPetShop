package com.thanhluu.tlcn.Controller;

import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.request.User.LoginRequest;
import com.thanhluu.tlcn.DTO.request.User.RequestOtpRequest;
import com.thanhluu.tlcn.DTO.request.User.VerifyOtpRegisterRequest;
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
        catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody @Validated RequestOtpRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByGmail(request.getGmail())) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Generate OTP
        String otpCode = otpService.generateOtp();
        
        // Lưu OTP
        otpService.saveOtp(request.getGmail(), otpCode);
        
        // Gửi OTP qua email
        javaMailUtil.sendOtpEmail(request.getGmail(), otpCode, request.getFullName());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP đã được gửi đến email của bạn");
        response.put("email", request.getGmail());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register-with-otp")
    public ResponseEntity<?> registerWithOtp(@RequestBody @Validated VerifyOtpRegisterRequest dto) {
        return new ResponseEntity<>(userService.registerWithOtp(dto), HttpStatus.CREATED);
    }
}
