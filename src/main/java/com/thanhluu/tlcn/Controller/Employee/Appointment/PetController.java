package com.thanhluu.tlcn.Controller.Employee.Appointment;


import com.thanhluu.tlcn.DTO.request.Pet.PetCreateRequest;
import com.thanhluu.tlcn.DTO.response.Pet.PetCreateResponse;
import com.thanhluu.tlcn.DTO.response.Pet.PetFindByCodeResponse;
import com.thanhluu.tlcn.Service.Employee.IPetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/pets")
public class PetController {

    @Autowired
    private IPetService petService;

    @GetMapping("/{code}")
    public ResponseEntity<?> findByPetCode(@PathVariable String code) {

        try {
            PetFindByCodeResponse petResponseDTOFindByCode = petService.findByCode(code);
            return new ResponseEntity<>(petResponseDTOFindByCode, HttpStatus.OK);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }


    @PostMapping("/create-pet")
    public ResponseEntity<?> createPet(@RequestBody PetCreateRequest pet_CreateRequest) {

        try {
            PetCreateResponse petResponseDTOCreate = petService.save(pet_CreateRequest);
            return new ResponseEntity<>(petResponseDTOCreate, HttpStatus.CREATED);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>( e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
