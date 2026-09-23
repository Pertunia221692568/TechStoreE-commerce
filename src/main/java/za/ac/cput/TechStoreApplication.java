/* TechStoreApplication.java
 * Author: Pertunia Sifunda(221692568)
 */
package za.ac.cput;

import za.ac.cput.domain.*;
import za.ac.cput.factory.*;
import za.ac.cput.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * TechStoreApplication – Main  Application
 * Seeds demo data into MySQL on startup.
 */
@SpringBootApplication
public class TechStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechStoreApplication.class, args);
    }

    /**
     * It Seeds demo data into MySQL when the app starts.
     * Only runs if no products exist yet (to prevent duplicate seeding).
     */
    @Bean
    CommandLineRunner seedData(IProductsRepository productRepo,
                               ICustomerRepository customerRepo,
                               IOrderRepository orderRepo) {
        return args -> {

            // Only seed if database is empty
            if (productRepo.count() > 0) {
                System.out.println("Database already has data");
                return;
            }

            System.out.println("Seeding demo data into ecommerce_db...");

            // Seed Products
            Products p1 = productRepo.save(ProductsFactory.createLaptop(
                    "Dell XPS 15 9530", "Dell",
                    "15.6\" OLED display, Intel i7, 32GB RAM, 1TB SSD",
                    new BigDecimal("34999.99"), 8));

            Products p2 = productRepo.save(ProductsFactory.createLaptop(
                    "HP Spectre x360 14", "HP",
                    "2-in-1 laptop, Intel i7, 16GB RAM, 512GB SSD",
                    new BigDecimal("27999.00"), 12));

            Products p3 = productRepo.save(ProductsFactory.createLaptop(
                    "Lenovo ThinkPad X1 Carbon", "Lenovo",
                    "Business ultrabook, Intel i5, 16GB RAM, 512GB SSD",
                    new BigDecimal("22499.00"), 6));

            Products p4 = productRepo.save(ProductsFactory.createLaptop(
                    "Apple MacBook Air M3", "Apple",
                    "13.6\" Liquid Retina, M3 chip, 8GB RAM, 256GB SSD",
                    new BigDecimal("24999.00"), 15));

            Products p5 = productRepo.save(ProductsFactory.createAccessory(
                    "Logitech MX Master 3S", "Logitech",
                    "Advanced wireless mouse, 8K DPI sensor",
                    new BigDecimal("1299.00"), 30));

            Products p6 = productRepo.save(ProductsFactory.createAccessory(
                    "Samsung 27\" 4K Monitor", "Samsung",
                    "UHD IPS panel, 60Hz, HDR10, USB-C",
                    new BigDecimal("7999.00"), 10));

            Products p7 = productRepo.save(ProductsFactory.create(
                    "SteelSeries Arctis Nova 7", "SteelSeries",
                    "Wireless gaming headset, 38hr battery, lossless 2.4GHz",
                    new BigDecimal("2299.00"), 20, "Headset"));

            // ─ Seed Customers
            Customer c1 = customerRepo.save(CustomerFactory.create(
                    "Pertunia", "Sifunda", "pertunia@techstore.co.za",
                    "071 234 5678", "10 Long Street, Cape Town, 8001"));

            Customer c2 = customerRepo.save(CustomerFactory.create(
                    "Ntlantla", "Slayi", "ntlantla@gmail.com",
                    "082 345 6789", "22 Barrack Street, CapeTown 8001"));

            Customer c3 = customerRepo.save(CustomerFactory.create(
                    "Abongile", "Zinja", "ZinjaA@mycput.ac.za",
                    "064 456 7890", "40 Sir lowry rd, CapeTown 8001"));

            // Seed Orders
            // Order 1 – Pertunia buys a Dell laptop
            Order o1 = OrderFactory.create(c1, c1.getAddress());
            OrderItem oi1 = OrderItemFactory.create(o1, p1, 1);
            o1.addOrderItem(oi1);
            o1.setStatus(Order.Status.CONFIRMED);
            orderRepo.save(o1);

            // Order 2 – Ntlantla buys a MacBook and a mouse
            Order o2 = OrderFactory.create(c2, c2.getAddress());
            OrderItem oi2a = OrderItemFactory.create(o2, p4, 1);
            OrderItem oi2b = OrderItemFactory.create(o2, p5, 1);
            o2.addOrderItem(oi2a);
            o2.addOrderItem(oi2b);
            o2.setStatus(Order.Status.SHIPPED);
            orderRepo.save(o2);

            // Order 3 – Abongile buys a monitor
            Order o3 = OrderFactory.create(c3, c3.getAddress());
            OrderItem oi3 = OrderItemFactory.create(o3, p6, 2);
            o3.addOrderItem(oi3);
            orderRepo.save(o3);

            System.out.println("Demo data seeded successfully!");
            System.out.println("   Products:  " + productRepo.count());
            System.out.println("   Customers: " + customerRepo.count());
            System.out.println("   Orders:    " + orderRepo.count());
            System.out.println("🚀 TechStore API running at http://localhost:8080");
        };
    }
}

