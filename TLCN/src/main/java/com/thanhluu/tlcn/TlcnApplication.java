package com.thanhluu.tlcn;

import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.Role;
import com.thanhluu.tlcn.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class TlcnApplication implements CommandLineRunner {

//    @Autowired
//    private UserRepository userRepository;
//
//    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        SpringApplication.run(TlcnApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Chỉ thêm admin nếu chưa tồn tại

//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            Date dobDate = formatter.parse("2004-07-03");
//
//            UserEntity user = UserEntity.builder()
//                    .gmail("admin@gmail.com")
//                    .fullName("Bùi Thị Trinh")
//                    .password(passwordEncoder.encode("123"))
//                    .gender("Nữ")
//                    .phoneNumber("04727382947")
//                    .address("31 Hàm Nghi, Thành Phố Huế")
//                    .dob(dobDate)
//                    .role(Role.ADMIN)
//                    .build();
//
//            userRepository.save(user);
//            System.out.println("Admin user created!");

    }
}