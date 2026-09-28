package za.ac.cput.repository;

import za.ac.cput.domain.ShippingAddress;
import za.ac.cput.factory.ShippingAddressFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IShippingAddressRepositoryTest {

    @Autowired
    private IShippingAddressRepository repository;

    @Test
    void testSave() {
        ShippingAddress address = ShippingAddressFactory.createAddress(
                "123 Main Street",
                "Apt 4B",
                "Cape Town",
                "Western Cape",
                "8000",
                "South Africa"
        );

        assertNotNull(address);

        ShippingAddress saved = repository.save(address);

        assertNotNull(saved);
        assertEquals("123 Main Street", saved.getAddressLine1());
        assertEquals("Apt 4B", saved.getAddressLine2());
        assertEquals("Cape Town", saved.getCity());
        assertEquals("Western Cape", saved.getState());
        assertEquals("8000", saved.getZipcode());
        assertEquals("South Africa", saved.getCountry());
    }

    @Test
    void testFindAll() {
        assertNotNull(repository.findAll());
    }
}