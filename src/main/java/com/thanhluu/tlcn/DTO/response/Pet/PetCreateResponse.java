package com.thanhluu.tlcn.DTO.response.Pet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetCreateResponse {

    private String petCode;

    private String name;

    private String breed;

    private String type;

    private Integer age;

    private UUID ownerId;
}
