package hotel.domain.pricing;

import hotel.domain.models.Guest;

/**
 * Decides which pricing rules (decorators) apply to a guest and stacks them.
 * The FrontDesk only asks for "the calculator for this guest", so the choice of
 * rules is not hard-coded in the facade.
 */
public interface PricingPolicy {

    PriceCalculator pricingFor(Guest guest);
}
