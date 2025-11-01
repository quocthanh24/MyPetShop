package com.thanhluu.tlcn.Repository;


import com.thanhluu.tlcn.Enum.Role;
import com.thanhluu.tlcn.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByGmail(String gmail);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    
    Page<UserEntity> findByRole(Role role, Pageable pageable);

    Optional<UserEntity> findById(UUID id);


}
