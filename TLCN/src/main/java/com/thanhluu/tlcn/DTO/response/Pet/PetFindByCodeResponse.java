package com.thanhluu.tlcn.DTO.response.Pet;


import com.thanhluu.tlcn.DTO.response.Pet.Owner.OwnerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetFindByCodeResponse {

    private String name;

    private String type;

    private String breed;

    private Integer age;

    private OwnerResponse owner;

}
