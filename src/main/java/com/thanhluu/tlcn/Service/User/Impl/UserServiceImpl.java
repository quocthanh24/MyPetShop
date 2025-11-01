package com.thanhluu.tlcn.Service.User.Impl;


import com.thanhluu.tlcn.DTO.request.User.UserRequest;
import com.thanhluu.tlcn.DTO.response.User.UserResponse;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Enum.Role;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.UserMapper;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.User.IUserService;
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
    public UserResponse register(UserRequest dto) {
        System.out.println(dto.toString());
        UserEntity userEntity = userMapper.toEntity(dto);
        System.out.println(userEntity.toString());
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRole(Role.CUSTOMER);
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
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


}
