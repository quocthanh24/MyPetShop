package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity,String> {

}
