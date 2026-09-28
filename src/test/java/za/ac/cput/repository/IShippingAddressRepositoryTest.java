package za.ac.cput.repository;

import za.ac.cput.domain.ShippingAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IShippingAddressRepositoryTest {

    @Autowired
    private IShippingAddressRepository repository;

    @Test
    @DisplayName("Should save a shipping address")
    void testSave() {
        ShippingAddress address = new ShippingAddress(
                "10 Long Street", "Unit 5", "Cape Town",
                "Western Cape", "8001", "South Africa");

        ShippingAddress saved = repository.save(address);

        assertNotNull(saved.getId());
        assertEquals("Cape Town", saved.getCity());
    }

    @Test
    @DisplayName("Should find all shipping addresses")
    void testFindAll() {
        repository.save(new ShippingAddress(
                "1 Test St", "", "Johannesburg",
                "Gauteng", "2000", "South Africa"));

        List<ShippingAddress> all = repository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @DisplayName("Should find shipping address by ID")
    void testFindById() {
        ShippingAddress saved = repository.save(new ShippingAddress(
                "2 Test St", "", "Durban",
                "KZN", "4001", "South Africa"));

        Optional<ShippingAddress> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Durban", found.get().getCity());
    }
}