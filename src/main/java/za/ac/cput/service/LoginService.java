package za.ac.cput.service;


import java.util.List;
import za.ac.cput.domain.Login;
import za.ac.cput.repository.ILoginRepository;

public class LoginService {

    private final ILoginRepository repository;

    public LoginService(ILoginRepository repository) {
        this.repository = repository;
    }

    public Login create(Login login) {
        return repository.save(login);
    }

    public Login get(String id) {
        return repository.findById(id);
    }

    public List<Login> getAll() {
        return repository.findAll();
    }

    public Login update(Login login) {
        return repository.update(login);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
