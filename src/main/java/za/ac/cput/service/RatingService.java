package za.ac.cput.service;
import java.util.List;
import za.ac.cput.domain.Rating;
import za.ac.cput.repository.IRatingRepository;

public class RatingService {


        private final IRatingRepository repository;

        public RatingService(IRatingRepository repository) {
            this.repository = repository;
        }

        public Rating create(Rating rating) {
            return repository.save(rating);
        }

        public Rating get(String id) {
            return repository.findById(id);
        }

        public List<Rating> getAll() {
            return repository.findAll();
        }

        public Rating update(Rating rating) {
            return repository.update(rating);
        }

        public boolean delete(String id) {
            return repository.delete(id);
        }
    }


