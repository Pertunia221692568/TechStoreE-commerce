/* UserService
 * TechStoreE-commerce – ADP372S
 */
package za.ac.cput.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import za.ac.cput.factory.UserFactory;
import za.ac.cput.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUserRepository repository;

    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public User createCustomerUser(String email, String passwordHash,
                                   String firstName, String lastName, String phone) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        User user = UserFactory.createCustomerUser(email, passwordHash, firstName, lastName, phone);
        return repository.save(user);
    }

    @Transactional
    public User createAdminUser(String email, String passwordHash,
                                String firstName, String lastName) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        User user = UserFactory.createAdminUser(email, passwordHash, firstName, lastName);
        return repository.save(user);
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public List<User> findByRole(Role role) {
        return repository.findByRole(role);
    }

    @Transactional
    public User updateLastLogin(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setLastLoginAt(LocalDateTime.now());
        return repository.save(user);
    }

    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
