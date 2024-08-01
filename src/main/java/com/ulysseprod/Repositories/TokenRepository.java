package com.ulysseprod.Repositories;


import com.ulysseprod.Entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {


    Optional<Token> findByToken(String token);


    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expiresAt > CURRENT_TIMESTAMP ")
    List<Token> findAllValidTokensByUser(Integer userId);


}
