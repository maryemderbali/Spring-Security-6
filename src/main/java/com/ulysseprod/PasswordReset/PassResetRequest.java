package com.ulysseprod.PasswordReset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassResetRequest {
    private String newPassword;
    private String confirmPassword;


}
