package za.ac.cput.repository;

import za.ac.cput.domain.Login;
import za.ac.cput.factory.LoginFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ILoginRepositoryTest {

    @Autowired
    private ILoginRepository repository;

    @Test
    void testSave() {
        Login login = LoginFactory.createLogin("L101", "john_doe", "hashed_password_123");

        assertNotNull(login);

        Login saved = repository.save(login);

        assertNotNull(saved);
        assertEquals("L101", saved.getLoginId());
        assertEquals("john_doe", saved.getUsername());
        assertEquals("hashed_password_123", saved.getPasswordHash());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}