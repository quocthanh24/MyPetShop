package com.thanhluu.tlcn.DTO.response.Pet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet_MRDResponse {

    private String name;

    private String age;

    private String type;
}
