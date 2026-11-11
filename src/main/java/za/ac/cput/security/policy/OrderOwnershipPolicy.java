package za.ac.cput.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import za.ac.cput.domain.Order;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.repository.IUserRepository;

/**
 * Policy: A customer may only view / update / cancel orders that belong to them.
 * Admins are always allowed.
 *
 * Usage example in a service or controller:
 *   if (!orderOwnershipPolicy.canAccess(order)) {
 *       throw new AccessDeniedException("You do not own this order");
 *   }
 */
@Component
public class OrderOwnershipPolicy {

    private final IUserRepository userRepository;

    public OrderOwnershipPolicy(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks whether the currently authenticated user is allowed to access the given order.
     */
    public boolean canAccess(Order order) {
        if (order == null) {
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return false;
        }

        // Admins can access any order
        if (currentUser.getRole() == Role.ADMIN) {
            return true;
        }

        // Customer may only access their own orders
        if (order.getCustomer() == null || order.getCustomer().getUser() == null) {
            return false;
        }

        return order.getCustomer().getUser().getId().equals(currentUser.getId());
    }

    /**
     * Convenience method that throws if access is denied.
     */
    public void checkAccess(Order order) {
        if (!canAccess(order)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Access denied: you do not own this order");
        }
    }
}

