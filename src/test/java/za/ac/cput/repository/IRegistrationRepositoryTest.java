package za.ac.cput.repository;

import za.ac.cput.domain.Registration;
import za.ac.cput.factory.RegistrationFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IRegistrationRepositoryTest {

    @Autowired
    private IRegistrationRepository repository;

    @Test
    void testSave() {
        Registration registration = RegistrationFactory.createRegistration("REG101", "john@example.com", "John Doe", "hashed_pwd_123");

        assertNotNull(registration);

        Registration saved = repository.save(registration);

        assertNotNull(saved);
        assertEquals("REG101", saved.getRegistrationId());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("John Doe", saved.getFullName());
        assertEquals("hashed_pwd_123", saved.getPasswordHash());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}