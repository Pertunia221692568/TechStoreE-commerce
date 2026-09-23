package za.ac.cput.controller;

import java.util.List;
import za.ac.cput.domain.Registration;
import za.ac.cput.service.RegistrationService;

public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    public Registration create(Registration registration) {
        return service.create(registration);
    }

    public Registration get(String id) {
        return service.get(id);
    }

    public List<Registration> getAll() {
        return service.getAll();
    }

    public Registration update(Registration registration) {
        return service.update(registration);
    }

    public boolean delete(String id) {
        return service.delete(id);
    }
}
