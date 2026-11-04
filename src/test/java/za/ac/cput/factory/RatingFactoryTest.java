package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Rating;

import static org.junit.jupiter.api.Assertions.*;

public class RatingFactoryTest {

    @Test
    void testCreateRating() {
        Rating rating = RatingFactory.createRating(5, "Excellent product", 1L);
        assertNotNull(rating);
        assertEquals(5, rating.getScore());
        assertEquals("Excellent product", rating.getComment());
        assertEquals(1L, rating.getProductId());
        assertNotNull(rating.getRatingDate());
    }

    @Test
    void testCreateRatingFailMissingProduct() {
        Rating rating = RatingFactory.createRating(4, "Good", null);
        assertNull(rating);
    }

    @Test
    void testCreateRatingFailBadScore() {
        Rating rating = RatingFactory.createRating(0, "Bad score", 1L);
        assertNull(rating);
    }

    @Test
    void testCreateRatingFailEmptyComment() {
        Rating rating = RatingFactory.createRating(5, "  ", 1L);
        assertNull(rating);
    }
}