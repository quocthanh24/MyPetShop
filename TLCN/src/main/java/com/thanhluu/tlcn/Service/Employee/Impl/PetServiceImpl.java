package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.Pet.PetCreateRequest;
import com.thanhluu.tlcn.DTO.response.Pet.PetCreateResponse;
import com.thanhluu.tlcn.DTO.response.Pet.PetFindByCodeResponse;
import com.thanhluu.tlcn.Entity.PetEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Mapper.PetMapper;
import com.thanhluu.tlcn.Repository.PetRepository;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Employee.IPetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements IPetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetMapper petMapper;

    @Override
    public PetFindByCodeResponse findByCode(String code) {

        PetEntity petEntity = petRepository.findByPetCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with code: " + code));


        return petMapper.toDTO_FindByCode(petEntity);
    }

    @Override
    public PetCreateResponse save(PetCreateRequest pet_CreateRequest) {

        UserEntity userEntity = userRepository.findByPhoneNumber(pet_CreateRequest.getOwner_phone_number())
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        PetEntity petEntity = petMapper.toEntity(pet_CreateRequest);
        petEntity.setOwner(userEntity);
        petRepository.save(petEntity);
        return petMapper.toDTO_Create(petEntity);

    }

}
