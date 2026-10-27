package hotel.domain.pricing;

import hotel.domain.models.Reservation;

/**
 * -10% of the price calculated so far, for loyalty members.
 */
public class LoyaltyDiscount extends PriceAdjustment {

    private static final double RATE = 0.10;

    public LoyaltyDiscount(PriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    protected double adjustment(Reservation reservation, double priceSoFar) {
        return -priceSoFar * RATE;
    }

    @Override
    protected String label() {
        return "loyalty discount";
    }
}
