package com.ulysseprod.PasswordReset;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PassResetController {
    @Autowired
    PassResetService passResetService;


    @PostMapping("/Forgot-Password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable("email") String email) {
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
    public ResponseEntity<String> resetPassword(@RequestParam ("token") String token, @RequestBody PassResetRequest passResetRequest) {
        try {
            passResetService.resetPassword(token, passResetRequest);
            return ResponseEntity.ok("Password reset successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
        }
    }
}
