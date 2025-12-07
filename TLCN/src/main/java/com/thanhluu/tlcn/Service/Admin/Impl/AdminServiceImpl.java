package com.thanhluu.tlcn.Service.Admin.Impl;

import com.thanhluu.tlcn.DTO.response.User.User_PromotedResponse;
import com.thanhluu.tlcn.DTO.response.User.User_UnpromotedResponse;
import com.thanhluu.tlcn.Enum.Role;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Mapper.UserMapper;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Admin.IAdminService;
import com.thanhluu.tlcn.Service.User.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserService userService;

    @Override
    public Page<User_UnpromotedResponse> getAllUsers(Pageable pageable) {
        Page<UserEntity> entities = userRepository.findByRole(Role.CUSTOMER, pageable);
        return entities.map(entity -> userMapper.toDTO_Unpromoted(entity));
    }

    @Override
    public User_PromotedResponse promoteUserToEmployee(String id) {
        UserEntity userEntity = userService.findById(id);
        userEntity.setRole(Role.EMPLOYEE);
        userService.save(userEntity);
        return userMapper.toDTO_Promoted(userEntity);
    }


}
