package com.ulysseprod.Authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePassRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
}
