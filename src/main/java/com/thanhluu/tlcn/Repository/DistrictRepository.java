package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, String> {
  @Query(value = """
      SELECT * FROM districts
      WHERE name_extension @> jsonb_build_array(:name)
      """,
    nativeQuery = true)
  List<DistrictEntity> findDistinctByName(@Param("name") String districtName);
}
