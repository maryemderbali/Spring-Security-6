package com.ulysseprod.Authentication;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulysseprod.Entities.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String token;

    @JsonProperty("refresh_token")
    private String refreshToken;


    private TokenType tokenType;

    private Collection<? extends GrantedAuthority> authorities;



}
