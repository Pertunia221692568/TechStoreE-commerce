package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Registration;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationFactoryTest {

    @Test
    void testCreateRegistration() {

        Registration registration = RegistrationFactory.createRegistration("john@example.com", "hashed_pwd_123");

        assertNotNull(registration);
        assertEquals("john@example.com", registration.getEmail());
        assertEquals("hashed_pwd_123", registration.getPassword());
        assertNotNull(registration.getRegistrationDate());
    }

    @Test
    void testCreateRegistrationFail() {

        Registration registration = RegistrationFactory.createRegistration("", "hashed_pwd_123");
        assertNull(registration); // Now expects null because validation failed
    }
}