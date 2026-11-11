package za.ac.cput.security.password;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Enforces a strong password policy for TechStore.
 *
 * Rules (industry-aligned for student/production balance):
 *  - Minimum 8 characters
 *  - At least one uppercase letter
 *  - At least one lowercase letter
 *  - At least one digit
 *  - At least one special character from a safe set
 *
 * This class is used on both registration and password-reset so the
 * same rules are applied consistently.
 */
@Component
public class PasswordPolicy {

    private static final int MIN_LENGTH = 8;

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT     = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL   = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    /**
     * Validates the password and returns a list of human-readable error messages.
     * An empty list means the password is acceptable.
     */
    public List<String> validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isBlank()) {
            errors.add("Password is required");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (!UPPERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (!LOWERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }
        if (!DIGIT.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }
        if (!SPECIAL.matcher(password).find()) {
            errors.add("Password must contain at least one special character (!@#$%^&* etc.)");
        }

        return errors;
    }

    /**
     * Throws IllegalArgumentException if the password does not meet the policy.
     */
    public void enforce(String password) {
        List<String> errors = validate(password);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}

