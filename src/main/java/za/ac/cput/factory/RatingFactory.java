package za.ac.cput.factory;


import java.time.LocalDateTime;
import za.ac.cput.domain.Rating;

public class RatingFactory {

    public static Rating createRating(
            String ratingId,
            Integer score,
            String comment) {

        return new Rating(
                score,
                comment,
                LocalDateTime.now(),
                ratingId
        );
    }
}
