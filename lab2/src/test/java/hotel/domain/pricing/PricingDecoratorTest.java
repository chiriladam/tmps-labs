package hotel.domain.pricing;

import hotel.domain.models.DoubleRoom;
import hotel.domain.models.Guest;
import hotel.domain.models.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Decorator: pricing rules wrap each other through the same PriceCalculator interface.
 * Stay used everywhere: double room (90 EUR), 2 guests, Thu Nov 12 -> Sun Nov 15 2026
 * = 3 nights, 2 of them weekend nights (Fri, Sat).
 * Base 270.00, weekend surcharge 2 x 90 x 20% = 36.00, tourist tax 2 x 3 x 1.50 = 9.00.
 */
class PricingDecoratorTest {

    private static final double CENT = 0.001;

    private final Reservation stay = new Reservation.Builder("Maria Popescu", new DoubleRoom(201),
            LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 15))
            .guests(2)
            .build();

    @Test
    void eachDecoratorAddsItsAmountOnTopOfTheWrappedCalculator() {
        PriceCalculator pricing = new TouristTax(new WeekendSurcharge(new BasePrice()));

        assertEquals(315.00, pricing.calculate(stay), CENT);
        assertEquals("room and extras 270.00 + weekend surcharge 36.00 + tourist tax 9.00",
                pricing.breakdown(stay));
    }

    @Test
    void fixedAmountRulesGiveTheSameTotalInAnyOrder() {
        PriceCalculator surchargeThenTax = new TouristTax(new WeekendSurcharge(new BasePrice()));
        PriceCalculator taxThenSurcharge = new WeekendSurcharge(new TouristTax(new BasePrice()));

        assertEquals(surchargeThenTax.calculate(stay), taxThenSurcharge.calculate(stay), CENT);
    }

    @Test
    void percentageDiscountDependsOnOrderSoThePolicyAddsTheTaxLast() {
        Guest loyal = new Guest("Ion Rusu", true);
        // tax wrapped INSIDE the discount: the tax would be discounted too -> 315.00 - 10% = 283.50
        PriceCalculator wrongOrder = new LoyaltyDiscount(new TouristTax(new WeekendSurcharge(new BasePrice())));
        // the standard policy wraps the tax last: 306.00 - 30.60 + 9.00 = 284.40
        PriceCalculator policy = new RuleBasedPricingPolicy(List.of(
                PricingRule.always(WeekendSurcharge::new),
                PricingRule.when(Guest::loyaltyMember, LoyaltyDiscount::new),
                PricingRule.always(TouristTax::new))).pricingFor(loyal);

        assertEquals(283.50, wrongOrder.calculate(stay), CENT);
        assertEquals(284.40, policy.calculate(stay), CENT);
        assertNotEquals(wrongOrder.calculate(stay), policy.calculate(stay), CENT);
    }

    @Test
    void policyStacksOnlyTheRulesThatApplyToTheGuest() {
        PricingPolicy policy = new RuleBasedPricingPolicy(List.of(
                PricingRule.when(Guest::loyaltyMember, LoyaltyDiscount::new)));

        String regular = policy.pricingFor(new Guest("Maria Popescu", false)).breakdown(stay);
        String loyal = policy.pricingFor(new Guest("Ion Rusu", true)).breakdown(stay);

        assertFalse(regular.contains("loyalty discount"));
        assertTrue(loyal.contains("loyalty discount 27.00"));
    }

    // a rule that does not exist in the project: written here to prove that a new
    // decorator plugs in without changing PriceAdjustment, the policy or the FrontDesk
    static class EarlyBirdDiscount extends PriceAdjustment {
        EarlyBirdDiscount(PriceCalculator wrapped) {
            super(wrapped);
        }

        @Override
        protected double adjustment(Reservation reservation, double priceSoFar) {
            return -20;
        }

        @Override
        protected String label() {
            return "early bird";
        }
    }

    @Test
    void newDecoratorIsAddedWithoutChangingExistingClasses() {
        PricingPolicy policy = new RuleBasedPricingPolicy(List.of(
                PricingRule.always(WeekendSurcharge::new),
                PricingRule.always(EarlyBirdDiscount::new),
                PricingRule.always(TouristTax::new)));

        PriceCalculator pricing = policy.pricingFor(new Guest("Maria Popescu", false));

        assertEquals(295.00, pricing.calculate(stay), CENT);
        assertEquals("room and extras 270.00 + weekend surcharge 36.00 - early bird 20.00 + tourist tax 9.00",
                pricing.breakdown(stay));
    }
}
