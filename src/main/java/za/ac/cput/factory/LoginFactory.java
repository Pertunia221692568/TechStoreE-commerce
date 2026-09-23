package za.ac.cput.factory;

import java.time.LocalDateTime;
import za.ac.cput.domain.Login;

public class LoginFactory {

    public static Login createLogin(
            String loginId,
            String username,
            String passwordHash) {

        return new Login(
                loginId,
                username,
                passwordHash,
                LocalDateTime.now(),
                null
        );
    }
}

