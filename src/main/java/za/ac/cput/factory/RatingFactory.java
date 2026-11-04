package za.ac.cput.factory;

import java.time.LocalDateTime;
import za.ac.cput.domain.Rating;

public class RatingFactory {

    public static Rating createRating(Integer score, String comment, Long productId) {
        if (score == null || score < 1 || score > 5) {
            return null;
        }
        if (comment == null || comment.isBlank()) {
            return null;
        }
        if (productId == null) {
            return null;
        }
        return new Rating(score, comment.trim(), LocalDateTime.now(), productId);
    }
}