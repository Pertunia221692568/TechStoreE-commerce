/* ProductsFactory.java class
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Products;
import java.math.BigDecimal;


public class ProductsFactory {

    /** Creates a fully specified Product. */
    public static Products create(String name, String brand, String description,
                                 BigDecimal price, int stockQuantity, String category) {
        return new Products.Builder()
                .name(name)
                .brand(brand)
                .description(description)
                .price(price)
                .stockQuantity(stockQuantity)
                .category(category)
                .build();
    }


    public static Products createLaptop(String name, String brand,
                                       String description, BigDecimal price, int stock) {
        return new Products.Builder()
                .name(name)
                .brand(brand)
                .description(description)
                .price(price)
                .stockQuantity(stock)
                .category("Laptop")
                .build();
    }

    /** Creates an accessory (mouse, keyboard, headset, etc.) */
    public static Products createAccessory(String name, String brand,
                                          String description, BigDecimal price, int stock) {
        return new Products.Builder()
                .name(name)
                .brand(brand)
                .description(description)
                .price(price)
                .stockQuantity(stock)
                .category("Accessory")
                .build();
    }
}

