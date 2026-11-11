package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.security.verification.PasswordResetToken;

import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByCodeAndUsedFalse(String code);

    Optional<PasswordResetToken> findByUser_IdAndUsedFalse(Long userId);

    void deleteByUser_Id(Long userId);
}

