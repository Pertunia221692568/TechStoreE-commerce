package za.ac.cput.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.repository.IUserRepository;

/**
 * Policy: Only users with the CUSTOMER role may perform the action.
 * Useful for endpoints that are meaningless or dangerous for admins
 * (e.g. placing an order as a customer, managing a personal cart).
 */
@Component
public class CustomerOnlyPolicy implements SecurityPolicy {

    private final IUserRepository userRepository;

    public CustomerOnlyPolicy(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isAllowed() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return userRepository.findByEmail(auth.getName())
                .map(user -> user.getRole() == Role.CUSTOMER)
                .orElse(false);
    }

    public void check() {
        if (!isAllowed()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Access denied: this action is only available to customers");
        }
    }
}

