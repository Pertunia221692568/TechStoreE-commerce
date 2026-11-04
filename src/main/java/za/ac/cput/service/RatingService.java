package za.ac.cput.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.domain.Rating;
import za.ac.cput.factory.RatingFactory;
import za.ac.cput.repository.IRatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final IRatingRepository repository;

    public RatingService(IRatingRepository repository) {
        this.repository = repository;
    }

    public Rating create(Rating rating) {
        if (rating.getProductId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        if (rating.getScore() == null || rating.getScore() < 1 || rating.getScore() > 5) {
            throw new IllegalArgumentException("score must be between 1 and 5");
        }
        if (rating.getRatingDate() == null) {
            rating.setRatingDate(java.time.LocalDateTime.now());
        }
        return repository.save(rating);
    }

    public Rating create(Integer score, String comment, Long productId) {
        Rating rating = RatingFactory.createRating(score, comment, productId);
        if (rating == null) {
            throw new IllegalArgumentException("Invalid rating data");
        }
        return repository.save(rating);
    }

    public Optional<Rating> get(Long id) {
        return repository.findById(id);
    }

    public List<Rating> getAll() {
        return repository.findAll();
    }

    public List<Rating> getByProductId(Long productId) {
        return repository.findByProductIdOrderByRatingDateDesc(productId);
    }

    public Rating update(Rating rating) {
        return repository.save(rating);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}