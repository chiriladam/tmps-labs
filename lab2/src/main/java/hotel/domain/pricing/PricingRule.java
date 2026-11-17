package hotel.domain.pricing;

import hotel.domain.models.Guest;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * One entry of the pricing policy: WHICH guests a rule applies to, and HOW to wrap
 * the calculator built so far (usually the decorator's constructor, e.g. TouristTax::new).
 */
public record PricingRule(Predicate<Guest> appliesTo, UnaryOperator<PriceCalculator> decorator) {

    public static PricingRule always(UnaryOperator<PriceCalculator> decorator) {
        return new PricingRule(guest -> true, decorator);
    }

    public static PricingRule when(Predicate<Guest> condition, UnaryOperator<PriceCalculator> decorator) {
        return new PricingRule(condition, decorator);
    }
}
