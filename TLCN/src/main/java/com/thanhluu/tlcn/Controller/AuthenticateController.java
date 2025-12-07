package com.thanhluu.tlcn.Controller;


import com.thanhluu.tlcn.Service.User.Impl.UserServiceImpl;
import com.thanhluu.tlcn.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticateController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            String userId = jwtUtil.extractUserId(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Kiểm tra refreshToken có hợp lệ không
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(username,userId);
                return ResponseEntity.ok(Map.of(
                            "userId", userId,
                        "access_token", newAccessToken,
                        "refresh_token", refreshToken
                         ));
            }
            else {
                return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
                return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

}
