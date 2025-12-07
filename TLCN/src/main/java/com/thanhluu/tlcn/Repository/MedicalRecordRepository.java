package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.MedicalRecordEntity;
import com.thanhluu.tlcn.Entity.PetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, UUID> {

    Optional<MedicalRecordEntity> findById(UUID id);
    @Query(
      "SELECT m FROM MedicalRecordEntity m " +
        "WHERE m.pet.owner.phoneNumber = :phoneNumber")
    Page<MedicalRecordEntity> findByOwnerPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

}
