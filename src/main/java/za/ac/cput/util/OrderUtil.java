/* Utility class for order-related helpers
 * Author: Pertunia Sifunda(221692568)
 * Date: 2026
 */
package za.ac.cput.util;

import za.ac.cput.domain.Order;
import java.time.format.DateTimeFormatter;

/**
 * Provides helper methods for Order display and formatting.
 */
public class OrderUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private OrderUtil() {} // Utility class — no instantiation

    /** Formats the order date as a readable string. */
    public static String formatOrderDate(Order order) {
        if (order.getOrderDate() == null) return "—";
        return order.getOrderDate().format(FORMATTER);
    }

    /** Returns  order summary. */
    public static String getSummary(Order order) {
        return String.format("Order #%d | %s | %d item(s) | R%.2f | %s",
                order.getId(),
                order.getCustomer().getFullName(),
                order.getOrderItems().size(),
                order.getTotalAmount(),
                order.getStatus());
    }

    /** Checks if an order qualifies for free delivery (over R500). */
    public static boolean qualifiesForFreeDelivery(Order order) {
        return order.getTotalAmount()
                .compareTo(java.math.BigDecimal.valueOf(500)) >= 0;
    }

    /** Returns the delivery fee for an order. */
    public static java.math.BigDecimal getDeliveryFee(Order order) {
        return qualifiesForFreeDelivery(order)
                ? java.math.BigDecimal.ZERO
                : java.math.BigDecimal.valueOf(99);
    }
}

