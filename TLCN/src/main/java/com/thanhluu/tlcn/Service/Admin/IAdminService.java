package com.thanhluu.tlcn.Service.Admin;

import com.thanhluu.tlcn.DTO.response.User.User_PromotedResponse;
import com.thanhluu.tlcn.DTO.response.User.User_UnpromotedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminService {

    Page<User_UnpromotedResponse> getAllUsers(Pageable pageable);

    User_PromotedResponse promoteUserToEmployee(String id);
}
