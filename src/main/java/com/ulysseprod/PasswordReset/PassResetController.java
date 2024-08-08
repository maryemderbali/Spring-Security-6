package com.ulysseprod.PasswordReset;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PassResetController {
    @Autowired
    PassResetService passResetService;


    @PostMapping("/Forgot-Password")
    public ResponseEntity<String> forgotPassword(@RequestParam ("email") String email) {
        try {
            passResetService.forgotPassword(email);
            return ResponseEntity.ok("Password Reset Email Sent Successfully");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed To Send.");
        }
    }

    @PostMapping("/Reset-Password")
    public ResponseEntity<String> resetPassword(@RequestParam ("token") String token, @RequestParam ("email") String newPassword) {
        try {
            passResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
        }
    }
}
