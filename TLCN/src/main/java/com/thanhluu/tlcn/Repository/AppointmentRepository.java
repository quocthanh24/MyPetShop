package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.AppointmentEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
  Page<AppointmentEntity> findAllByCustomerAndStatus(UserEntity customer, AppointmentStatus status, Pageable pageable);
}
