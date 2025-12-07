package com.thanhluu.tlcn.DTO.request.Pet;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet_MedicalRecordRequest {

    @NotEmpty(message = "Name should not be empty")
    private String name;

    @NotEmpty(message = "Type should not be empty")
    private String type;

    @NotEmpty(message = "Breed should not be empty")
    private String breed;

    @Positive(message = "Age should be greater than 0")
    private Integer age;


}
