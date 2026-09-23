
/* OrderItemFactory.java class
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Order;
import za.ac.cput.domain.OrderItem;
import za.ac.cput.domain.Products;


public class OrderItemFactory {

    /**
     * Creates an OrderItem using the product's current price as the unit price
     */
    public static OrderItem create(Order order, Products product, int quantity) {
        return new OrderItem.Builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
    }
}
