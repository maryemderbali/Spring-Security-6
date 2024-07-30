package com.ulysseprod.Authentication;

import com.ulysseprod.DTO.RoleName;
import com.ulysseprod.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private String username;
    private String password;
    private RoleName roleName;

}
