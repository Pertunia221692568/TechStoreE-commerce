/* OrderFactory class
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Customer;
import za.ac.cput.domain.Order;


public class OrderFactory {


    public static Order create(Customer customer, String shippingAddress) {
        return new Order.Builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .build();
    }

    /** Creates an order using the customer's saved address. */
    public static Order createWithCustomerAddress(Customer customer) {
        return new Order.Builder()
                .customer(customer)
                .shippingAddress(customer.getAddress())
                .build();
    }

    /** Creates an order with a custom shipping address and notes. */
    public static Order createWithNotes(Customer customer,
                                        String shippingAddress, String notes) {
        return new Order.Builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .notes(notes)
                .build();
    }
}

