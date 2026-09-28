package za.ac.cput.repository;

import za.ac.cput.domain.Rating;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IRatingRepositoryTest {

    @Autowired
    private IRatingRepository repository;

    @Test
    @DisplayName("Should save a rating")
    void testSave() {
        Rating rating = new Rating(5, "Excellent product", LocalDateTime.now());

        Rating saved = repository.save(rating);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getScore());
    }

    @Test
    @DisplayName("Should find all ratings")
    void testFindAll() {
        repository.save(new Rating(4, "Good", LocalDateTime.now()));

        List<Rating> all = repository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @DisplayName("Should find rating by ID")
    void testFindById() {
        Rating saved = repository.save(new Rating(3, "Average", LocalDateTime.now()));

        Optional<Rating> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(3, found.get().getScore());
    }
}