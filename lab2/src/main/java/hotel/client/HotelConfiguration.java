package hotel.client;

import hotel.domain.builder.ReservationDirector;
import hotel.domain.calendar.IcsCalendarExporter;
import hotel.domain.factory.DoubleRoomFactory;
import hotel.domain.factory.RoomFactory;
import hotel.domain.factory.SingleRoomFactory;
import hotel.domain.factory.SuiteFactory;
import hotel.domain.models.Guest;
import hotel.domain.models.RoomType;
import hotel.domain.pricing.LoyaltyDiscount;
import hotel.domain.pricing.PricingPolicy;
import hotel.domain.pricing.PricingRule;
import hotel.domain.pricing.RuleBasedPricingPolicy;
import hotel.domain.pricing.TouristTax;
import hotel.domain.pricing.WeekendSurcharge;
import hotel.domain.service.FrontDesk;
import hotel.domain.service.InMemoryBookingSystem;
import hotel.domain.service.ReservationNumberGenerator;

import java.util.List;
import java.util.Map;

/**
 * The composition root: the only place that knows the concrete classes and wires them
 * together. Everything else depends on interfaces. Adding a pricing rule, a room type
 * or another calendar format means adding a class and registering it here.
 */
public final class HotelConfiguration {

    private HotelConfiguration() {
    }

    // the order matters: the tourist tax is added last, so taxes are never discounted
    public static PricingPolicy standardPricing() {
        return new RuleBasedPricingPolicy(List.of(
                PricingRule.always(WeekendSurcharge::new),
                PricingRule.when(Guest::loyaltyMember, LoyaltyDiscount::new),
                PricingRule.always(TouristTax::new)));
    }

    public static FrontDesk frontDesk() {
        Map<RoomType, RoomFactory> roomFactories = Map.of(
                RoomType.SINGLE, new SingleRoomFactory(),
                RoomType.DOUBLE, new DoubleRoomFactory(),
                RoomType.SUITE, new SuiteFactory());
        return new FrontDesk(
                new InMemoryBookingSystem(ReservationNumberGenerator.getInstance()),
                roomFactories,
                new ReservationDirector(),
                standardPricing(),
                new IcsCalendarExporter());
    }
}
