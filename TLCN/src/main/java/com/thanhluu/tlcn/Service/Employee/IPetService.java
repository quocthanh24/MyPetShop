package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.Pet.PetCreateRequest;
import com.thanhluu.tlcn.DTO.response.Pet.PetCreateResponse;
import com.thanhluu.tlcn.DTO.response.Pet.PetFindByCodeResponse;


public interface IPetService {

    PetFindByCodeResponse findByCode(String code);

    PetCreateResponse save(PetCreateRequest pet_CreateRequest);
}
