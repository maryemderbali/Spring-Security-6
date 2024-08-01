package com.ulysseprod.Authentication;

//import com.ulysseprod.Email.EmailService;
//import com.ulysseprod.Email.EmailTemplateName;
import com.ulysseprod.Entities.Role;
import com.ulysseprod.Entities.Token;
import com.ulysseprod.Entities.TokenType;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.TokenRepository;
import com.ulysseprod.Repositories.UserRepository;
import com.ulysseprod.config.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
//    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) throws MessagingException {


        String roleName;
        try {
            roleName = request.getRoleName();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();


        repository.save(user);
        var jwtToken =jwtService.generateToken(user);
        saveUserToken(user, jwtToken);
        sendValidationEmail(user);


        return AuthenticationResponse.builder().token(jwtToken).tokenType(TokenType.BEARER).build();
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

//        emailService.sendEmail(
//                user.getEmail(),
//                user.getUsername(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                "http://localhost:4200/activate-account",
//                newToken,
//                "Account activation"
//        );
    }


    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = repository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        repository.save(user);

        tokenRepository.delete(savedToken);
    }

    private void revokeAllUserTokens(User user){
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setExpiresAt(LocalDateTime.now());
            token.setRevoked(true);        });
        tokenRepository.saveAll(validTokens);
    }


    private String generateAndSaveActivationToken(User user) {
            String generatedToken= generateActivationCode(6);
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
        for (int i= 0; i<length ; i++ )
        {
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
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: "
                        + request.getUsername()));

        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
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
                .expiresAt(LocalDateTime.now().plusSeconds(+1000 * 60 * 24))
                .revoked(false)
                .expired(false)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

}
