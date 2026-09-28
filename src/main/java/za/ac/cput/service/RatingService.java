package za.ac.cput.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.domain.Rating;
import za.ac.cput.repository.IRatingRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final IRatingRepository repository;

    public RatingService(IRatingRepository repository) {
        this.repository = repository;
    }

    public Rating create(Rating rating) {
        return repository.save(rating);
    }

    public Optional<Rating> get(Long id) {
        return repository.findById(id);
    }

    public List<Rating> getAll() {
        return repository.findAll();
    }

    public Rating update(Rating rating) {
        return repository.save(rating);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
