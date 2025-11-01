package com.thanhluu.tlcn.DTO.response.Pet.Owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerResponse {

    private String name;

    private String address;

    private String phone_number;

}
