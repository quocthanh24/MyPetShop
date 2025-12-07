package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "districts")
public class DistrictEntity {
  @Id
  private String districtId;
  private String districtName;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<String> nameExtension;

  @ManyToOne
  @JoinColumn(name = "province_id")
  private ProvinceEntity province;

  @OneToMany(mappedBy = "district", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WardEntity> wards;
}
