/* CustomerService.java
 * Author: Pertunia Sifunda (221692568)
 * Updated to work with User composition.
 */
package za.ac.cput.service;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.User;
import za.ac.cput.factory.CustomerFactory;
import za.ac.cput.repository.ICustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final ICustomerRepository customerRepository;
    private final UserService userService;

    public CustomerService(ICustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByUser_Email(email);
    }

    /**
     * Registers a new Customer together with its User identity.
     * passwordHash must already be hashed by the caller.
     */
    public Customer register(String firstName, String lastName,
                             String email, String phone, String address,
                             String passwordHash) {
        // Create the User first
        User user = userService.createCustomerUser(email, passwordHash, firstName, lastName, phone);

        // Then create the Customer profile linked to that User
        Customer customer = CustomerFactory.create(user, address);
        customer = customerRepository.save(customer);

        // Maintain bidirectional link
        user.setCustomer(customer);
        userService.save(user);

        return customer;
    }

    /**
     * Legacy-compatible overload (no password). Creates a User with a temporary hash.
     * Prefer the full register(...) method when password is available.
     */
    public Customer register(String firstName, String lastName,
                             String email, String phone, String address) {
        // Temporary placeholder hash – will be replaced when real security is added
        String tempHash = "TEMP_" + email.hashCode();
        return register(firstName, lastName, email, phone, address, tempHash);
    }

    public Customer update(Long id, String firstName, String lastName,
                           String email, String phone, String address) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        User user = customer.getUser();
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhone(phone);
            userService.save(user);
        }
        customer.setAddress(address);
        return customerRepository.save(customer);
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}