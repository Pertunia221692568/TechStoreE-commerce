/* Utility class for price formatting
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.util;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class PriceUtil {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.15"); // 15% South African VAT

    private PriceUtil() {} // Utility class — no instantiation

    /** Formats a price with 2 decimal places. */
    public static BigDecimal round(BigDecimal price) {
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /** Calculates VAT amount (15%) on a price. */
    public static BigDecimal calculateVat(BigDecimal price) {
        return round(price.multiply(VAT_RATE));
    }

    /** Returns price including VAT. */
    public static BigDecimal priceWithVat(BigDecimal price) {
        return round(price.add(calculateVat(price)));
    }

    /** Formats price as a South African Rand  */
    public static String formatRand(BigDecimal price) {
        return "R " + round(price).toPlainString();
    }

    /** Applies a percentage discount to a price. */
    public static BigDecimal applyDiscount(BigDecimal price, double discountPercent) {
        BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercent / 100));
        return round(price.subtract(discount));
    }
}

