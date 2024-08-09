package com.ulysseprod.PasswordReset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassResetTokenRepo extends JpaRepository<PasswordResetToken, Integer> {

    PasswordResetToken findByToken (String passwordResetToken);

    void deletePasswordResetTokenByUserId(Integer id);
}
