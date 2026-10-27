package hotel.domain.pricing;

import hotel.domain.models.Reservation;
import hotel.utilities.Money;

/**
 * A pricing rule that wraps another PriceCalculator: it takes the price calculated
 * so far and adds or removes its own amount. Because it is itself a PriceCalculator,
 * it can be wrapped again by the next rule.
 */
public abstract class PriceAdjustment implements PriceCalculator {

    private final PriceCalculator wrapped;

    protected PriceAdjustment(PriceCalculator wrapped) {
        this.wrapped = wrapped;
    }

    // positive = surcharge, negative = discount
    protected abstract double adjustment(Reservation reservation, double priceSoFar);

    protected abstract String label();

    @Override
    public double calculate(Reservation reservation) {
        double priceSoFar = wrapped.calculate(reservation);
        return priceSoFar + adjustment(reservation, priceSoFar);
    }

    @Override
    public String breakdown(Reservation reservation) {
        double amount = adjustment(reservation, wrapped.calculate(reservation));
        if (amount == 0) {
            return wrapped.breakdown(reservation);
        }
        return wrapped.breakdown(reservation)
                + (amount > 0 ? " + " : " - ") + label() + " " + Money.eur(Math.abs(amount));
    }
}
