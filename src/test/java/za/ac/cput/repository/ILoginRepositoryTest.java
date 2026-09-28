package za.ac.cput.repository;

import za.ac.cput.domain.Login;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ILoginRepositoryTest {

    @Autowired
    private ILoginRepository repository;

    @Test
    @DisplayName("Should save a login")
    void testSave() {
        Login login = new Login(
                "user" + System.currentTimeMillis(),
                "hashedPassword",
                LocalDateTime.now(),
                LocalDateTime.now());

        Login saved = repository.save(login);

        assertNotNull(saved.getId());
        assertNotNull(saved.getUsername());
    }

    @Test
    @DisplayName("Should find all logins")
    void testFindAll() {
        repository.save(new Login(
                "findAll" + System.currentTimeMillis(),
                "hash", LocalDateTime.now(), LocalDateTime.now()));

        List<Login> all = repository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @DisplayName("Should find login by ID")
    void testFindById() {
        Login saved = repository.save(new Login(
                "findById" + System.currentTimeMillis(),
                "hash", LocalDateTime.now(), LocalDateTime.now()));

        Optional<Login> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
    }
}