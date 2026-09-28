package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Rating;
import static org.junit.jupiter.api.Assertions.*;

public class RatingFactoryTest {

    @Test
    void testCreateRating() {
        Rating rating = RatingFactory.createRating("R101", 5, "Great service!");
        assertNotNull(rating);
        assertEquals("R101", rating.getRatingId());
        assertEquals(5, rating.getScore());
        assertEquals("Great service!", rating.getComment());
    }

    @Test
    void testCreateRatingFail() {
        Rating rating = RatingFactory.createRating("", 5, "Great service!");
        assertNotNull(rating);
        assertEquals("", rating.getRatingId());
    }
}