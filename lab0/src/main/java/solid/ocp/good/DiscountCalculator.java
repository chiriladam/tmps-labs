package solid.ocp.good;

/**
 * Does not know which discount types exist; it only works with the Discount interface.
 */
public class DiscountCalculator {
    public double calculate(Discount discount, double price) {
        return discount.apply(price);
    }
}
