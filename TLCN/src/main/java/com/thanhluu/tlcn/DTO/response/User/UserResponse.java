package com.thanhluu.tlcn.DTO.response.User;


import com.thanhluu.tlcn.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String gmail;

    private String fullName;

    private String gender;

    private String phoneNumber;

    private String address;

    private Date dob;

    private Role role;
}
