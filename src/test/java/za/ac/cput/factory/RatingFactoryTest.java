package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Rating;
import static org.junit.jupiter.api.Assertions.*;

public class RatingFactoryTest {

    @Test
    void testCreateRating() {

        Rating rating = RatingFactory.createRating(5, "Great service!");

        assertNotNull(rating);
        assertEquals(5, rating.getScore());
        assertEquals("Great service!", rating.getComment());
        assertNotNull(rating.getRatingDate());
    }

    @Test
    void testCreateRatingFail() {
        // Pass a null score to trigger the validation we added in Step1
        Rating rating = RatingFactory.createRating(null, "Great service!");

        assertNull(rating); // Expects null because validation failed
    }
}