package za.ac.cput.factory;


import za.ac.cput.domain.ShippingAddress;

public class ShippingAddressFactory {

    public static ShippingAddress createAddress(
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String zipcode,
            String country) {

        return new ShippingAddress(
                addressLine1,
                addressLine2,
                city,
                state,
                zipcode,
                country
        );
    }
}
