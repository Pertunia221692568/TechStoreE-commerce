package za.ac.cput.repository;

import za.ac.cput.domain.Rating;
import za.ac.cput.factory.RatingFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IRatingRepositoryTest {

    @Autowired
    private IRatingRepository repository;

    @Test
    void testSave() {
        Rating rating = RatingFactory.createRating("R101", 5, "Great service!");

        assertNotNull(rating);

        Rating saved = repository.save(rating);

        assertNotNull(saved);
        assertEquals("R101", saved.getRatingId());
        assertEquals(5, saved.getScore());
        assertEquals("Great service!", saved.getComment());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}