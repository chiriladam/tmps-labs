package hotel.domain.pricing;

import hotel.domain.models.Reservation;

/**
 * Calculates the final price of a reservation.
 * Pricing rules implement the same interface and wrap each other, so they can be
 * stacked in any combination at runtime.
 */
public interface PriceCalculator {

    double calculate(Reservation reservation);

    // human-readable list of every step, e.g. "room and extras 270.00 + tourist tax 9.00"
    String breakdown(Reservation reservation);
}
