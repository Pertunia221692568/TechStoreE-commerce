package za.ac.cput.service;

import java.util.List;
import za.ac.cput.domain.Registration;
import za.ac.cput.repository.IRegistrationRepository;
public class RegistrationService {


        private final IRegistrationRepository repository;

        public RegistrationService(IRegistrationRepository repository) {
            this.repository = repository;
        }

        public Registration create(Registration registration) {
            return repository.save(registration);
        }

        public Registration get(String id) {
            return repository.findById(id);
        }

        public List<Registration> getAll() {
            return repository.findAll();
        }

        public Registration update(Registration registration) {
            return repository.update(registration);
        }

        public boolean delete(String id) {
            return repository.delete(id);
        }
    }

