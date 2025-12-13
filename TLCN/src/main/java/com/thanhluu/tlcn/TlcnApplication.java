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
public class TlcnApplication{
    public static void main(String[] args) {
        SpringApplication.run(TlcnApplication.class, args);
    }
}