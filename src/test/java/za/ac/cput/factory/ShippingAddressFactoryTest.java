package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.ShippingAddress;
import static org.junit.jupiter.api.Assertions.*;

public class ShippingAddressFactoryTest {

    @Test
    void testCreateAddress() {
        ShippingAddress address = ShippingAddressFactory.createAddress(
                "123 Main Street",
                "Apt 4B",
                "Cape Town",
                "Western Cape",
                "8000",
                "South Africa"
        );
        assertNotNull(address);
        assertEquals("123 Main Street", address.getAddressLine1());
        assertEquals("Apt 4B", address.getAddressLine2());
        assertEquals("Cape Town", address.getCity());
        assertEquals("Western Cape", address.getState());
        assertEquals("8000", address.getZipcode());
        assertEquals("South Africa", address.getCountry());
    }

    @Test
    void testCreateAddressFail() {
        ShippingAddress address = ShippingAddressFactory.createAddress(
                "",
                "Apt 4B",
                "Cape Town",
                "Western Cape",
                "8000",
                "South Africa"
        );
        assertNotNull(address);
        assertEquals("", address.getAddressLine1());
    }
}