package com.wcs.travel_blog.auth.repository;

import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    void deleteByUser(User user);

    Optional<PasswordResetToken> findByToken(String token);
}
