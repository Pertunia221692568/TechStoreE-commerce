package za.ac.cput.security.auth;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brute-force protection: after {@value #MAX_FAILURES} consecutive failed logins
 * for an email, further logins for that email are refused for 15 minutes.
 * Kept in memory (resets when the server restarts), which is enough for this project.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_FAILURES = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(15);

    private record Attempt(int failures, Instant lockedUntil) {}

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isLocked(String email) {
        Attempt a = attempts.get(key(email));
        if (a == null || a.lockedUntil() == null) return false;
        if (Instant.now().isAfter(a.lockedUntil())) {
            attempts.remove(key(email));
            return false;
        }
        return true;
    }

    public void recordFailure(String email) {
        attempts.merge(key(email), new Attempt(1, null), (old, ignored) -> {
            int failures = old.failures() + 1;
            return new Attempt(failures,
                    failures >= MAX_FAILURES ? Instant.now().plus(LOCK_TIME) : null);
        });
    }

    public void recordSuccess(String email) {
        attempts.remove(key(email));
    }

    private String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
