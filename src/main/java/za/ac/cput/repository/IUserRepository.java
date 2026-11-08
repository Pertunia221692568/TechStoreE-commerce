/* User Repository Interface
 * TechStoreE-commerce – ADP372S
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Role;
import za.ac.cput.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    Optional<User> findByEmailAndEnabledTrue(String email);
}
