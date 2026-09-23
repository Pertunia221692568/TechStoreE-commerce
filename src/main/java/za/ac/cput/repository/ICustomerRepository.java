/* Customer Repository Interface
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
