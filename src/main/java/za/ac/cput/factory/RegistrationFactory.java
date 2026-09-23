package za.ac.cput.factory;



import java.time.LocalDateTime;
import za.ac.cput.domain.Registration;

public class RegistrationFactory {

    public static Registration createRegistration(
            String registrationId,
            String email,
            String fullName,
            String passwordHash) {

        return new Registration(
                registrationId,
                email,
                fullName,
                passwordHash,
                LocalDateTime.now()
        );
    }
}
