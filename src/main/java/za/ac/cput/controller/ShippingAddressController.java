package za.ac.cput.controller;


import java.util.List;
import za.ac.cput.domain.ShippingAddress;
import za.ac.cput.service.ShippingAddressService;

public class ShippingAddressController {

    private final ShippingAddressService service;

    public ShippingAddressController(ShippingAddressService service) {
        this.service = service;
    }

    public ShippingAddress create(ShippingAddress address) {
        return service.create(address);
    }

    public List<ShippingAddress> getAll() {
        return service.getAll();
    }

    public ShippingAddress update(ShippingAddress address) {
        return service.update(address);
    }

    public boolean delete(ShippingAddress address) {
        return service.delete(address);
    }
}
