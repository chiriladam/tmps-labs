package solid.ocp.good;

public class VipDiscount implements Discount {
    public double apply(double price) {
        return price * 0.80;  // 20% discount
    }
}
