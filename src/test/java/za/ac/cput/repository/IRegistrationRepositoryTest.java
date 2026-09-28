package za.ac.cput.repository;

import za.ac.cput.domain.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IRegistrationRepositoryTest {

    @Autowired
    private IRegistrationRepository repository;

    @Test
    @DisplayName("Should save a registration")
    void testSave() {
        Registration reg = new Registration(
                "user" + System.currentTimeMillis() + "@test.com",
                "password123",
                LocalDateTime.now());

        Registration saved = repository.save(reg);

        assertNotNull(saved.getId());
        assertNotNull(saved.getEmail());
    }

    @Test
    @DisplayName("Should find all registrations")
    void testFindAll() {
        repository.save(new Registration(
                "findall" + System.currentTimeMillis() + "@test.com",
                "pass", LocalDateTime.now()));

        List<Registration> all = repository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @DisplayName("Should find registration by ID")
    void testFindById() {
        Registration saved = repository.save(new Registration(
                "findbyid" + System.currentTimeMillis() + "@test.com",
                "pass", LocalDateTime.now()));

        Optional<Registration> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
    }
}