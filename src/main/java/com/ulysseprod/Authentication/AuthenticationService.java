package com.ulysseprod.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulysseprod.Email.EmailService;
import com.ulysseprod.Email.EmailTemplateName;
import com.ulysseprod.Entities.Role;
import com.ulysseprod.Entities.Token;
import com.ulysseprod.Entities.TokenType;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.TokenRepository;
import com.ulysseprod.Repositories.UserRepository;
import com.ulysseprod.config.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


        public AuthenticationResponse register(RegisterRequest request) throws MessagingException {


            Role role = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("USER");
                        return roleRepository.save(newRole);
                    });

            if (!isValidEmail(request.getEmail())) {
                logger.warn("Invalid email format: {}", request.getEmail());
                throw new RuntimeException("Invalid email format");
            }

            if (repository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Email already exists: {}", request.getEmail());
                throw new RuntimeException("Email already exists");
            }

            if (repository.findByUsername(request.getUsername()).isPresent()) {
                logger.warn("Username already exists: {}", request.getUsername());
                throw new RuntimeException("Username already exists");
            }

            var user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .blocked(false)
                    .enabled(false)
                    .roles(List.of(role))
                    .build();


            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(user, jwtToken);
            sendValidationEmail(user);

            logger.info("User registered successfully: {}", user.getUsername());


            return AuthenticationResponse.builder()
                    .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .refreshToken(refreshToken)
                .build();
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                "",
                newToken,
                "Account activation"
        );
    }


    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        var user = repository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        repository.save(user);

        tokenRepository.delete(savedToken);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setExpiresAt(LocalDateTime.now());
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }


    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .revoked(false)
                .tokenType(TokenType.ACTIVATION)
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: "
                        + request.getUsername()));

        if (!user.isEnabled() || user.isBlocked()) {
            throw new RuntimeException("User account is either disabled or blocked.");
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .authorities(user.getAuthorities())
                .build();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .token(jwtToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(1000 * 60 * 24))
                .revoked(false)
                .expired(false)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractusername(refreshToken);
        if (username != null) {
            var user = this.repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with username: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                logger.info("Token refreshed for user: {}", username);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}
