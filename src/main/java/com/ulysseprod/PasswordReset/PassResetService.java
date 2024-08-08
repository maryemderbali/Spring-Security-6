package com.ulysseprod.PasswordReset;

import com.ulysseprod.Email.EmailService;
import com.ulysseprod.Entities.Token;
import com.ulysseprod.Entities.TokenType;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.TokenRepository;
import com.ulysseprod.Repositories.UserRepository;
import com.ulysseprod.config.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassResetService {

    private final PassResetTokenRepo passResetTokenRepo;
    private final UserRepository repository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoer;
    public void forgotPassword(String email) throws MessagingException {
        Optional<User> userOptional = repository.findByEmail(email);
            User user = userOptional.get();

        String tokenValue = jwtService.generateToken(user);
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passResetTokenRepo.save(token);

        emailService.sendPasswordResetEmail(user.getEmail(), tokenValue);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken= passResetTokenRepo.findByToken(token);
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoer.encode(newPassword));
        repository.save(user);
    }

}
