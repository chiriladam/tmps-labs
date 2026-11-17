package hotel.domain.pricing;

import hotel.domain.models.Guest;

import java.util.List;

/**
 * Starts from the BasePrice and wraps it with every rule that applies to the guest,
 * in the order the rules were given. A new pricing rule is a new decorator class plus
 * one line in the list passed to this constructor; this class and the FrontDesk
 * stay unchanged.
 */
public class RuleBasedPricingPolicy implements PricingPolicy {

    private final List<PricingRule> rules;

    public RuleBasedPricingPolicy(List<PricingRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public PriceCalculator pricingFor(Guest guest) {
        PriceCalculator pricing = new BasePrice();
        for (PricingRule rule : rules) {
            if (rule.appliesTo().test(guest)) {
                pricing = rule.decorator().apply(pricing);
            }
        }
        return pricing;
    }
}
