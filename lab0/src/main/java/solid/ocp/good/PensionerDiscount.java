package solid.ocp.good;

/**
 * A NEW discount type, added without modifying DiscountCalculator.
 * This is "open for extension, closed for modification".
 */
public class PensionerDiscount implements Discount {
    public double apply(double price) {
        return price * 0.85;  // 15% discount
    }
}
