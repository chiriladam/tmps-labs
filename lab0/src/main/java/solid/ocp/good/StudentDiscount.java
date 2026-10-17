package solid.ocp.good;

public class StudentDiscount implements Discount {
    public double apply(double price) {
        return price * 0.90;  // 10% discount
    }
}
