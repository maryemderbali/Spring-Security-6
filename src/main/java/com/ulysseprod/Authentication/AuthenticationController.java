package com.ulysseprod.Authentication;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    @PostMapping("/SignUp")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
            throws MessagingException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/SignIn")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request)
    {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken (HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }


    @PostMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }
}
