package com.ulysseprod.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String token;
    private LocalDateTime createdAt;
    public LocalDateTime expiresAt;
    public boolean revoked;
    public boolean expired;
    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;


    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
