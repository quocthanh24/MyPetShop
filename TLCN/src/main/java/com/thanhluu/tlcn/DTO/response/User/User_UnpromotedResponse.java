package com.thanhluu.tlcn.DTO.response.User;

import com.thanhluu.tlcn.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_UnpromotedResponse {

    private UUID id;

    private String fullName;

    private String gender;

    private String phoneNumber;

    private String dob;

    private Role role;

}
