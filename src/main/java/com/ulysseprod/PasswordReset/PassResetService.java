package com.ulysseprod.PasswordReset;

import com.ulysseprod.Email.EmailService;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.UserRepository;
import com.ulysseprod.config.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PassResetService {

    private final PassResetTokenRepo passResetTokenRepo;
    private final UserRepository repository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    public void forgotPassword(String email) throws MessagingException {
        User user = repository.findUserByEmail(email);

        String tokenValue = jwtService.generateToken(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(2));
        passResetTokenRepo.save(token);

        // Call the method with username, email, and token
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), tokenValue);
    }

    public void resetPassword(String token, PassResetRequest passResetRequest) {

        PasswordResetToken passwordResetToken= passResetTokenRepo.findByToken(token);
        if (passwordResetToken == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        if (!passResetRequest.getNewPassword().equals(passResetRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(passResetRequest.getNewPassword()));
        repository.save(user);
    }

}
