package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.MedicalRecordDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalRecordDetailRepository extends JpaRepository<MedicalRecordDetailEntity, UUID> {

    Optional<MedicalRecordDetailEntity> findById(UUID id);
    Page<MedicalRecordDetailEntity> findByMedicalRecord_IdOrderByUpdatedDateDesc(UUID medicalRecordId, Pageable pageable);
}
