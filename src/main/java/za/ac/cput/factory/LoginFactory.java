package za.ac.cput.factory;

import java.time.LocalDateTime;
import za.ac.cput.domain.Login;

public class LoginFactory {

    public static Login createLogin(String username, String passwordHash) {

        if (username == null || username.isEmpty() || passwordHash == null || passwordHash.isEmpty()) {
            return null;
        }
        return new Login(username, passwordHash, LocalDateTime.now(), null);
    }
}