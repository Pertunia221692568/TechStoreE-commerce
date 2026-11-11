package za.ac.cput.security.verification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.User;
import za.ac.cput.repository.IEmailVerificationTokenRepository;
import za.ac.cput.repository.IPasswordResetTokenRepository;
import za.ac.cput.repository.IUserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles generation and validation of one-time tokens/codes for:
 *  - Email verification after registration
 *  - Password reset (forgot-password flow)
 *
 * Tokens expire after a short window and can be used only once.
 * In a real production system the code would also be emailed;
 * for this project the code is returned so the front-end / demo can show it.
 */
@Service
public class VerificationService {

    private static final int EMAIL_VERIFICATION_MINUTES = 30;
    private static final int PASSWORD_RESET_MINUTES = 15;

    private final IEmailVerificationTokenRepository emailTokenRepo;
    private final IPasswordResetTokenRepository resetTokenRepo;
    private final IUserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public VerificationService(IEmailVerificationTokenRepository emailTokenRepo,
                               IPasswordResetTokenRepository resetTokenRepo,
                               IUserRepository userRepository) {
        this.emailTokenRepo = emailTokenRepo;
        this.resetTokenRepo = resetTokenRepo;
        this.userRepository = userRepository;
    }

    // ─────────────────────────────────────────────────────────────
    // Email verification
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new email-verification token for the given user.
     * Any previous unused token for the same user is removed.
     *
     * @return the 6-digit code that the user must enter
     */
    @Transactional
    public String createEmailVerificationToken(User user) {
        emailTokenRepo.deleteByUser_Id(user.getId());

        String token = generateSecureToken();
        String code  = generateSixDigitCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_MINUTES);

        EmailVerificationToken verificationToken =
                new EmailVerificationToken(token, code, user, expiresAt);
        emailTokenRepo.save(verificationToken);

        // In production you would send an email here.
        // For the project we return the code so it can be displayed / logged.
        return code;
    }

    /**
     * Verifies the code, enables the user account, and marks the token as used.
     */
    @Transactional
    public User verifyEmail(String code) {
        EmailVerificationToken token = emailTokenRepo.findByCodeAndUsedFalse(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or already used verification code"));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new one.");
        }

        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        token.setUsed(true);
        emailTokenRepo.save(token);

        return user;
    }

    // ─────────────────────────────────────────────────────────────
    // Password reset
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a password-reset token for the user identified by email.
     * Returns the 6-digit code (or empty if the email does not exist –
     * we deliberately do not reveal whether the email is registered).
     */
    @Transactional
    public Optional<String> createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            // Do not leak whether the email exists
            return Optional.empty();
        }

        User user = userOpt.get();
        resetTokenRepo.deleteByUser_Id(user.getId());

        String token = generateSecureToken();
        String code  = generateSixDigitCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(PASSWORD_RESET_MINUTES);

        PasswordResetToken resetToken = new PasswordResetToken(token, code, user, expiresAt);
        resetTokenRepo.save(resetToken);

        return Optional.of(code);
    }

    /**
     * Validates the reset code and returns the associated user.
     * Does NOT change the password – that is done by the caller after
     * the new password has been checked against the PasswordPolicy.
     */
    @Transactional(readOnly = true)
    public User validatePasswordResetCode(String code) {
        PasswordResetToken token = resetTokenRepo.findByCodeAndUsedFalse(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or already used reset code"));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Reset code has expired. Please request a new one.");
        }

        return token.getUser();
    }

    /**
     * Marks the reset token as used after a successful password change.
     */
    @Transactional
    public void consumePasswordResetToken(String code) {
        resetTokenRepo.findByCodeAndUsedFalse(code.trim()).ifPresent(token -> {
            token.setUsed(true);
            resetTokenRepo.save(token);
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private String generateSecureToken() {
        // 32 random bytes → 64 hex characters
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String generateSixDigitCode() {
        int code = 100000 + secureRandom.nextInt(900000); // 100000–999999
        return String.valueOf(code);
    }
}
