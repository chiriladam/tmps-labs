package hotel.domain.pricing;

import hotel.domain.models.Reservation;

/**
 * City tourist tax: 1.50 EUR per guest per night.
 */
public class TouristTax extends PriceAdjustment {

    private static final double PER_GUEST_PER_NIGHT = 1.50;

    public TouristTax(PriceCalculator wrapped) {
        super(wrapped);
    }

    @Override
    protected double adjustment(Reservation reservation, double priceSoFar) {
        return reservation.getGuests() * reservation.getNights() * PER_GUEST_PER_NIGHT;
    }

    @Override
    protected String label() {
        return "tourist tax";
    }
}
