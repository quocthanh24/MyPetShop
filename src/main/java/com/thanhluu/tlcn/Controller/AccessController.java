package com.thanhluu.tlcn.Controller;

import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.request.User.UserLoginRequest;
import com.thanhluu.tlcn.DTO.response.Authen.AuthenResponse;
import com.thanhluu.tlcn.Service.User.IUserService;
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

@RestController
@RequestMapping("/api")
public class AccessController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userRequestDTOLogin) {

        // Áp dụng spring security để sinh token
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestDTOLogin.getGmail(), userRequestDTOLogin.getPassword()));

            if (authentication.isAuthenticated()) {

                String accessToken = jwtUtil.generateToken(userRequestDTOLogin.getGmail());
                String refreshToken = jwtUtil.generateRefreshToken(userRequestDTOLogin.getGmail());

                return new ResponseEntity<>(new AuthenResponse(accessToken, refreshToken), HttpStatus.OK);

            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated UserRequest dto) {
        return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
    }
}
