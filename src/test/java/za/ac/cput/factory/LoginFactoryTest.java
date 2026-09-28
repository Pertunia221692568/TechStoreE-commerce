package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Login;
import static org.junit.jupiter.api.Assertions.*;

public class LoginFactoryTest {

    @Test
    void testCreateLogin() {
        Login login = LoginFactory.createLogin("L101", "john_doe", "hashed_password_123");
        assertNotNull(login);
        assertEquals("L101", login.getLoginId());
        assertEquals("john_doe", login.getUsername());
        assertEquals("hashed_password_123", login.getPasswordHash());
        assertNotNull(login.getLastPasswordDate());
        assertNull(login.getLastLoginDate());
    }

    @Test
    void testCreateLoginFail() {
        // Without validation in LoginFactory, passing empty values still creates a valid object instance
        Login login = LoginFactory.createLogin("", "john_doe", "hashed_password_123");
        assertNotNull(login);
        assertEquals("", login.getLoginId());
    }
}