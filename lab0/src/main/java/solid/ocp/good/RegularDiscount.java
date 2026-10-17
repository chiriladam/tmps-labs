package solid.ocp.good;

public class RegularDiscount implements Discount {
    public double apply(double price) {
        return price * 0.95;  // 5% discount
    }
}
