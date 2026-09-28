package za.ac.cput.factory;

import java.time.LocalDateTime;
import za.ac.cput.domain.Rating;

public class RatingFactory {

    public static Rating createRating(Integer score, String comment) {

        if (score == null || comment == null || comment.isEmpty()) {
            return null;
        }
        return new Rating(score, comment, LocalDateTime.now());
    }
}