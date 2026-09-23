package za.ac.cput.controller;


import java.util.List;
import za.ac.cput.domain.Rating;
import za.ac.cput.service.RatingService;

public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    public Rating create(Rating rating) {
        return service.create(rating);
    }

    public Rating get(String id) {
        return service.get(id);
    }

    public List<Rating> getAll() {
        return service.getAll();
    }

    public Rating update(Rating rating) {
        return service.update(rating);
    }

    public boolean delete(String id) {
        return service.delete(id);
    }
}

