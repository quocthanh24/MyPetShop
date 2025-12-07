package com.thanhluu.tlcn.Controller.Admin;

import com.thanhluu.tlcn.Service.Admin.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class PromoteController {
    @Autowired
    private IAdminService adminService;

    @GetMapping("/user-account")
    public ResponseEntity<?> getPromotableAccount(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return new ResponseEntity<>(adminService.getAllUsers(pageable), HttpStatus.OK);
    }

    @PutMapping("/create-employee-account/{id}")
    public ResponseEntity<?> createEmployeeAccount(@PathVariable String id) {
        return new ResponseEntity<>(adminService.promoteUserToEmployee(id), HttpStatus.OK);
    }

}
