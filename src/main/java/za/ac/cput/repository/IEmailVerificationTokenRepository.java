package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.security.verification.EmailVerificationToken;

import java.util.Optional;

@Repository
public interface IEmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByCodeAndUsedFalse(String code);

    Optional<EmailVerificationToken> findByUser_IdAndUsedFalse(Long userId);

    void deleteByUser_Id(Long userId);
}
