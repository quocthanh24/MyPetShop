package com.thanhluu.tlcn.Controller.Employee.MedicalRecord;


import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.MedicalRecordDetailUpdateRequest;
import com.thanhluu.tlcn.Service.Employee.IMedicalRecordDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/med-records/details")
public class MedicalRecordDetailController {

    @Autowired
    private IMedicalRecordDetailService mrdService;


    // Lấy thông tin chi tiết của bệnh án sắp xếp theo thứ tự từ mới nhất đến cũ nhất
    @GetMapping("/{id}")
    public ResponseEntity<?> getMRD(@PathVariable String id,
                                    @PageableDefault(size = 5, sort = "updatedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(mrdService.getLatestMedicalRecordDetailById(id, pageable), HttpStatus.OK);
    }

    // Tạo mới thông tin chi tiết cho bệnh án
    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable String id,
                                                 @RequestBody MedicalRecordDetailUpdateRequest resqDTO){
        return new ResponseEntity<>(mrdService.updateMRD(resqDTO, id), HttpStatus.OK);
    }
}
