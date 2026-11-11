package za.ac.cput.security.policy;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import za.ac.cput.repository.ICustomerRepository;

/**
 * Policy: a customer may only act on their own customer record
 * (profile, orders placed under their customer id). Admins are always allowed.
 *
 * Mirrors {@link OrderOwnershipPolicy} so that authorisation rules stay out of controllers.
 */
@Component
public class CustomerOwnershipPolicy {

    private final ICustomerRepository customerRepository;

    public CustomerOwnershipPolicy(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /** True if the current principal is an admin or owns the given customer id. */
    public boolean canAccess(Long customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth) || customerId == null) {
            return false;
        }
        if (isAdmin(auth)) {
            return true;
        }
        return customerRepository.findById(customerId)
                .map(c -> c.getUser() != null
                        && c.getUser().getEmail().equalsIgnoreCase(auth.getName()))
                .orElse(false);
    }

    /** True if the current principal is an admin or the given email is their own. */
    public boolean canAccessEmail(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth) || email == null) {
            return false;
        }
        return isAdmin(auth) || email.trim().equalsIgnoreCase(auth.getName());
    }

    public void checkAccess(Long customerId) {
        if (!canAccess(customerId)) {
            throw new AccessDeniedException("Access denied: this is not your account");
        }
    }

    public void checkAccessEmail(String email) {
        if (!canAccessEmail(email)) {
            throw new AccessDeniedException("Access denied: this is not your account");
        }
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
    }

    private boolean isAdmin(Authentication auth) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(a.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
