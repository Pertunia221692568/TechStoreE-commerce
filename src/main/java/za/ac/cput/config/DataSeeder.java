package za.ac.cput.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import za.ac.cput.repository.IProductsRepository;
import za.ac.cput.service.ProductsService;

import java.math.BigDecimal;

/**
 * Adds starter products to the database the first time the backend runs
 * (only when the products table is empty).
 * Pictures live in: frontend/public/images/products/
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProducts(IProductsRepository repo, ProductsService service) {
        return args -> {
            if (repo.count() > 0) return;

            // name, brand, description, price, stock, category, image, condition
            add(service, "Dell XPS 13", "Dell", "13.3\" laptop, Intel i7, 16GB RAM, 512GB SSD", "24999.00", 5, "Laptop", "dell-xps-13.jpg", "New");
            add(service, "HP Pavilion 15", "HP", "15.6\" laptop, Ryzen 5, 8GB RAM, 256GB SSD", "11999.00", 8, "Laptop", "hp-pavilion-15.jpg", "New");
            add(service, "Lenovo ThinkPad T480", "Lenovo", "Refurbished business laptop, i5, 8GB RAM, 256GB SSD", "5499.00", 4, "Laptop", "thinkpad-t480.jpg", "Second-hand");
            add(service, "Dell OptiPlex 7050", "Dell", "Desktop tower, i5, 8GB RAM, 500GB HDD", "3999.00", 6, "Desktop", "optiplex-7050.jpg", "Second-hand");
            add(service, "HP Pro Tower 400", "HP", "Desktop tower, i7, 16GB RAM, 512GB SSD", "13499.00", 3, "Desktop", "hp-pro-tower.jpg", "New");
            add(service, "Samsung Galaxy S23", "Samsung", "128GB, 8GB RAM, 6.1\" display", "13999.00", 10, "Phone", "galaxy-s23.jpg", "New");
            add(service, "iPhone 12", "Apple", "64GB, good condition, battery health 85%", "6999.00", 4, "Phone", "iphone-12.jpg", "Second-hand");
            add(service, "Logitech MX Master 3", "Logitech", "Wireless ergonomic mouse", "1799.00", 15, "Mouse", "mx-master-3.jpg", "New");
            add(service, "Razer DeathAdder Essential", "Razer", "Wired gaming mouse, lightly used", "349.00", 7, "Mouse", "razer-deathadder.jpg", "Second-hand");
            add(service, "Keychron K2", "Keychron", "Wireless mechanical keyboard", "1899.00", 9, "Keyboard", "keychron-k2.jpg", "New");
            add(service, "Logitech K120", "Logitech", "Wired full-size keyboard", "249.00", 20, "Keyboard", "logitech-k120.jpg", "New");
            add(service, "LG 24\" Full HD Monitor", "LG", "24 inch IPS monitor, HDMI", "2299.00", 6, "Monitor", "lg-24-monitor.jpg", "New");
            add(service, "Samsung 27\" Curved Monitor", "Samsung", "27 inch curved monitor, 75Hz", "1999.00", 3, "Monitor", "samsung-27-curved.jpg", "Second-hand");
            add(service, "Sony WH-CH520 Headphones", "Sony", "Wireless on-ear headphones", "999.00", 12, "Accessory", "sony-wh-ch520.jpg", "New");
        };
    }

    private void add(ProductsService s, String name, String brand, String desc, String price,
                     int stock, String category, String image, String condition) {
        s.create(name, brand, desc, new BigDecimal(price), stock, category,
                "/images/products/" + image, condition);
    }
}

