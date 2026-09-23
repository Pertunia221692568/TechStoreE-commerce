package za.ac.cput.controller;


import java.util.List;
import za.ac.cput.domain.Login;
import za.ac.cput.service.LoginService;

public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }

    public Login create(Login login) {
        return service.create(login);
    }

    public Login get(String id) {
        return service.get(id);
    }

    public List<Login> getAll() {
        return service.getAll();
    }

    public Login update(Login login) {
        return service.update(login);
    }

    public boolean delete(String id) {
        return service.delete(id);
    }
}

