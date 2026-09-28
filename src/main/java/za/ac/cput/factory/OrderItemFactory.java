/* OrderItemFactory.java class
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Order;
import za.ac.cput.domain.OrderItem;
import za.ac.cput.domain.Products;

public class OrderItemFactory {

    public static OrderItem create(Order order, Products product, int quantity) {

        if (product == null) {
            throw new IllegalStateException("Product is required for an OrderItem");
        }

        // Use the Builder pattern (as defined in your OrderItem.java)
        return new OrderItem.Builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
    }
}