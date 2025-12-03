package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.DistrictEntity;
import com.thanhluu.tlcn.Entity.WardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<WardEntity, String> {
    @Query(value = """
      SELECT * FROM wards
      WHERE name_extension @> jsonb_build_array(:name)
      """,
      nativeQuery = true)
    List<WardEntity> findWardByName(@Param("name") String wardName);

}
