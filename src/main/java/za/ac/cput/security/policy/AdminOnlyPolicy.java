package za.ac.cput.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.repository.IUserRepository;

/**
 * Policy: Only users with the ADMIN role may perform the action.
 * Used for administrative operations that should never be reachable by customers.
 */
@Component
public class AdminOnlyPolicy implements SecurityPolicy {

    private final IUserRepository userRepository;

    public AdminOnlyPolicy(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isAllowed() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return userRepository.findByEmail(auth.getName())
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public void check() {
        if (!isAllowed()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Access denied: administrator privileges required");
        }
    }
}
