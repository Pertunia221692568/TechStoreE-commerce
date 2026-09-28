package za.ac.cput.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.domain.Login;
import za.ac.cput.repository.ILoginRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final ILoginRepository repository;

    public LoginService(ILoginRepository repository) {
        this.repository = repository;
    }

    public Login create(Login login) {
        return repository.save(login);
    }

    public Optional<Login> get(Long id) {
        return repository.findById(id);
    }

    public List<Login> getAll() {
        return repository.findAll();
    }

    public Login update(Login login) {
        return repository.save(login);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
