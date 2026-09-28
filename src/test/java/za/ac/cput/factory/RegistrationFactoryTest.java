package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Registration;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationFactoryTest {

    @Test
    void testCreateRegistration() {
        Registration registration = RegistrationFactory.createRegistration("REG101", "john@example.com", "John Doe", "hashed_pwd_123");
        assertNotNull(registration);
        assertEquals("REG101", registration.getRegistrationId());
        assertEquals("john@example.com", registration.getEmail());
        assertEquals("John Doe", registration.getFullName());
        assertEquals("hashed_pwd_123", registration.getPasswordHash());
    }

    @Test
    void testCreateRegistrationFail() {
        Registration registration = RegistrationFactory.createRegistration("", "john@example.com", "John Doe", "hashed_pwd_123");
        assertNotNull(registration);
        assertEquals("", registration.getRegistrationId());
    }
}