/* CustomerService.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.service;

import za.ac.cput.domain.Customer;
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

    public CustomerService(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() { return customerRepository.findAll(); }

    public Optional<Customer> findById(Long id) { return customerRepository.findById(id); }

    public Optional<Customer> findByEmail(String email) { return customerRepository.findByEmail(email); }

    public Customer register(String firstName, String lastName,
                             String email, String phone, String address) {
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        Customer customer = CustomerFactory.create(firstName, lastName, email, phone, address);
        return customerRepository.save(customer);
    }

    public Customer update(Long id, String firstName, String lastName,
                           String email, String phone, String address) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        return customerRepository.save(customer);
    }

    public void delete(Long id) { customerRepository.deleteById(id); }
}
