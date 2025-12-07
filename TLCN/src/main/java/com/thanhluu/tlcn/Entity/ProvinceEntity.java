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
@Table(name = "provinces")
public class ProvinceEntity {
  @Id
  private String provinceId;
  @Column(unique = true)
  private String provinceName;
  private String code;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<String> nameExtension;

  @OneToMany(mappedBy = "province", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<DistrictEntity> districts;
}
