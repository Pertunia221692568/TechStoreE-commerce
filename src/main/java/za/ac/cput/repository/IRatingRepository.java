package za.ac.cput.repository;


import za.ac.cput.domain.Rating;
import java.util.List;

public interface IRatingRepository {


        Rating save(Rating rating);

        Rating findById(String ratingId);

        List<Rating> findAll();

        Rating update(Rating rating);

        boolean delete(String ratingId);
    }

