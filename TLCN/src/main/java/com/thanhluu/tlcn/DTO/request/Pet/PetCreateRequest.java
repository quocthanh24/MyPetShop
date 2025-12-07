package com.thanhluu.tlcn.DTO.request.Pet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetCreateRequest {

    @NotEmpty(message = "Name should not be empty")
    private String name;

    @NotEmpty(message = "Type should not be empty")
    private String type;

    @NotEmpty(message = "Breed should not be empty")
    private String breed;

    @Positive(message = "Age should be greater than 0")
    private Integer age;

    @Valid
    @NotEmpty(message = "Owner phone number should not be empty")
    private String owner_phone_number;


}
