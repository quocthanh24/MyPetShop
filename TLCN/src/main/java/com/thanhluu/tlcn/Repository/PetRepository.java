package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, UUID> {

    Optional<PetEntity> findByPetCode(String petCode);

}
