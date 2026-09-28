package za.ac.cput.factory;

import java.time.LocalDateTime;
import za.ac.cput.domain.Registration;

public class RegistrationFactory {

    public static Registration createRegistration(String email, String password) {

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return new Registration(email, password, LocalDateTime.now());
    }
}