package com.wcs.travel_blog.auth.repository;

import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    void deleteByUser(User user);

    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT t FROM PasswordResetToken t JOIN FETCH t.user WHERE t.id = :id")
    Optional<PasswordResetToken> findByIdWithUser(@Param("id")  long id);
}
