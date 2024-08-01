package com.ulysseprod.Authentication;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulysseprod.Entities.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String token;

    private TokenType tokenType;




}
