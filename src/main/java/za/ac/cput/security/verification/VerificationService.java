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
 * Codes are delivered by {@link EmailService}; this class only creates and validates them.
 */
@Service
public class VerificationService {

    private static final int EMAIL_VERIFICATION_MINUTES = 30;
    private static final int PASSWORD_RESET_MINUTES = 15;
    private static final int MAX_ATTEMPTS = 5;
    private static final String INVALID_EMAIL_CODE = "Invalid or already used verification code";
    private static final String INVALID_RESET_CODE = "Invalid or already used reset code";

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

    public int emailVerificationMinutes() { return EMAIL_VERIFICATION_MINUTES; }

    public int passwordResetMinutes() { return PASSWORD_RESET_MINUTES; }

    /**
     * Creates a new email-verification token for the given user.
     * Any previous token for the same user is removed.
     *
     * @return the 6-digit code that must be delivered to the user (by email)
     */
    @Transactional
    public String createEmailVerificationToken(User user) {
        emailTokenRepo.deleteByUser_Id(user.getId());
        emailTokenRepo.flush();

        String token = generateSecureToken();
        String code  = generateSixDigitCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_MINUTES);

        emailTokenRepo.save(new EmailVerificationToken(token, code, user, expiresAt));
        return code;
    }

    /**
     * Verifies the code for the given email, enables the account and marks the token as used.
     * The code is only accepted for the account it was issued to, and is locked after
     * {@value #MAX_ATTEMPTS} wrong guesses.
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public User verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_EMAIL_CODE));

        EmailVerificationToken token = emailTokenRepo.findByUser_IdAndUsedFalse(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_EMAIL_CODE));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new one.");
        }
        if (token.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Too many incorrect attempts. Please request a new code.");
        }
        if (!codesMatch(token.getCode(), code)) {
            token.incrementAttempts();
            emailTokenRepo.save(token);
            throw new IllegalArgumentException(INVALID_EMAIL_CODE);
        }

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
            return Optional.empty();
        }

        User user = userOpt.get();
        resetTokenRepo.deleteByUser_Id(user.getId());
        resetTokenRepo.flush();

        String token = generateSecureToken();
        String code  = generateSixDigitCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(PASSWORD_RESET_MINUTES);

        resetTokenRepo.save(new PasswordResetToken(token, code, user, expiresAt));
        return Optional.of(code);
    }

    /**
     * Validates the reset code for the given email and returns the user.
     * Does NOT change the password – the caller does that after checking the PasswordPolicy.
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public User validatePasswordResetCode(String email, String code) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_RESET_CODE));

        PasswordResetToken token = resetTokenRepo.findByUser_IdAndUsedFalse(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_RESET_CODE));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Reset code has expired. Please request a new one.");
        }
        if (token.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Too many incorrect attempts. Please request a new code.");
        }
        if (!codesMatch(token.getCode(), code)) {
            token.incrementAttempts();
            resetTokenRepo.save(token);
            throw new IllegalArgumentException(INVALID_RESET_CODE);
        }
        return user;
    }

    /** Marks the user's reset token as used after a successful password change. */
    @Transactional
    public void consumePasswordResetToken(User user) {
        resetTokenRepo.findByUser_IdAndUsedFalse(user.getId()).ifPresent(token -> {
            token.setUsed(true);
            resetTokenRepo.save(token);
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private boolean codesMatch(String expected, String supplied) {
        if (expected == null || supplied == null) return false;
        return java.security.MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                supplied.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

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
