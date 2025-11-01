package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.MedicalRecordDetailEnitty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalRecordDetailRepository extends JpaRepository<MedicalRecordDetailEnitty, UUID> {

    Optional<MedicalRecordDetailEnitty> findById(UUID id);

    Page<MedicalRecordDetailEnitty> findByMedicalRecord_IdOrderByUpdatedDateDesc(UUID id, Pageable pageable);
}
