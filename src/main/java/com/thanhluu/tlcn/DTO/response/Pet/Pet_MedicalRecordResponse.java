package com.thanhluu.tlcn.DTO.response.Pet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet_MedicalRecordResponse {

    private String name;

    private String type;

    private String breed;

    private Integer age;
}
