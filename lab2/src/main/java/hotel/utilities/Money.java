package hotel.utilities;

import java.util.Locale;

/**
 * Formats amounts the same way on every computer (always "315.00", never "315,00").
 */
public final class Money {
    private Money() {
    }

    public static String eur(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}
