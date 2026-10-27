package hotel.domain.pricing;

import hotel.domain.models.Reservation;
import hotel.utilities.Money;

/**
 * The starting point of every price: nights x room price plus the chosen extras.
 */
public class BasePrice implements PriceCalculator {

    @Override
    public double calculate(Reservation reservation) {
        return reservation.getTotalPrice();
    }

    @Override
    public String breakdown(Reservation reservation) {
        return "room and extras " + Money.eur(calculate(reservation));
    }
}
