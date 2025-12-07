package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.MedicalRecord.MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.response.MedicalRecord.MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.ViewAllDetailByPhoneNumberResp;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.User.User_MedicalRecordResponse;
import com.thanhluu.tlcn.Entity.MedicalRecordDetailEntity;
import com.thanhluu.tlcn.Entity.MedicalRecordEntity;
import com.thanhluu.tlcn.Entity.PetEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.MedicalRecordMapper;
import com.thanhluu.tlcn.Mapper.PetMapper;
import com.thanhluu.tlcn.Mapper.UserMapper;
import com.thanhluu.tlcn.Repository.MedicalRecordDetailRepository;
import com.thanhluu.tlcn.Repository.MedicalRecordRepository;
import com.thanhluu.tlcn.Repository.PetRepository;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Employee.IMedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordServiceImpl implements IMedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicalRecordDetailRepository mrdRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;


    @Override
    public MedicalRecordResponse createMedicalRecordEntity(MedicalRecordRequest medicalRecordRequest) {

        // Lấy thông tin của chủ sở hữu thú cưng, bao gồm sdt
        // Tìm chủ sở hữu bằng sdt rồi truyền vào entity

        UserEntity owner = userRepository.findByPhoneNumber(medicalRecordRequest.getOwner().getPhoneNumber())
                .orElseThrow(() -> new BadRequestException(ErrorCode.OWNER_NOT_FOUND));


        // Lấy thông tin của thú cưng rồi truyền vào entity
        // Set chủ sở hữu cho thú cưng
        PetEntity petEntity = petMapper.toEntity_MedicalRecord(medicalRecordRequest.getPet());
        petEntity.setOwner(owner);

        petRepository.save(petEntity);

        // Lấy thông tin của nhân viên tạo bệnh án
        String employeePhonenumber = medicalRecordRequest.getEmployeePhoneNumber();

        UserEntity employee = userRepository.findByPhoneNumber(employeePhonenumber)
                .orElseThrow(() -> new BadRequestException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // Lấy thông tin về bệnh án rồi truyền vào entity

        MedicalRecordEntity medicalRecordEntity = medicalRecordMapper.toEntity(medicalRecordRequest);
        medicalRecordEntity.setPet(petEntity);
        medicalRecordEntity.setEmployee(employee);

        medicalRecordRepository.save(medicalRecordEntity);
        // Cuối cùng,trả về MedicalRecordRespDTO

        // Lấy thông tin về chi tiết bệnh án rồi truyền về entity

        MedicalRecordDetailEntity mrdEntity = medicalRecordMapper.toEntity_MRD(medicalRecordRequest);
        mrdEntity.setMedicalRecord(medicalRecordEntity);

        mrdRepository.save(mrdEntity);

        // Trước khi trả về, chuyển các entity -> respDTO
        Pet_MedicalRecordResponse petRespDTO = petMapper.toDTO_MedicalRecord(petEntity);
        User_MedicalRecordResponse ownerRespDTO = userMapper.toDTO_MedicalRecord(owner);
        MRDResponse mrdRespDTO = medicalRecordMapper.toDTO_MRD(mrdEntity);
        return medicalRecordMapper.toDTO(medicalRecordEntity, petRespDTO, ownerRespDTO, mrdRespDTO);
    }

    @Override
    public Page<ViewAllDetailByPhoneNumberResp> getAllMedicalRecordsByCustomerPhoneNumber(String phoneNumber, Pageable pageable) {
        Page<MedicalRecordEntity> medicalRecords = medicalRecordRepository.findByOwnerPhoneNumber(phoneNumber, pageable);
        return medicalRecords.map(medicalRecordMapper::toDTO_ViewAllDetails);
    }

}
