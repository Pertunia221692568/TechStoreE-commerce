package za.ac.cput.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.domain.Registration;
import za.ac.cput.repository.IRegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final IRegistrationRepository repository;

    public RegistrationService(IRegistrationRepository repository) {
        this.repository = repository;
    }

    public Registration create(Registration registration) {
        return repository.save(registration);
    }

    public Optional<Registration> get(Long id) {
        return repository.findById(id);
    }

    public List<Registration> getAll() {
        return repository.findAll();
    }

    public Registration update(Registration registration) {
        return repository.save(registration);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

