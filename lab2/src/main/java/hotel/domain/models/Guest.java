package hotel.domain.models;

/**
 * A hotel guest. Loyalty members receive a discount on their stays.
 */
public record Guest(String name, boolean loyaltyMember) {
}
