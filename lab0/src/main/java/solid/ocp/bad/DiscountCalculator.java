package solid.ocp.bad;

/**
 * OCP VIOLATION: every new customer type requires modifying
 * this method (one more else if).
 */
public class DiscountCalculator {
    public double calculate(String customerType, double price) {
        if (customerType.equals("REGULAR")) {
            return price * 0.95;  // 5% discount
        } else if (customerType.equals("VIP")) {
            return price * 0.80;  // 20% discount
        } else if (customerType.equals("STUDENT")) {
            return price * 0.90;  // 10% discount
        }
        return price;
    }
}
