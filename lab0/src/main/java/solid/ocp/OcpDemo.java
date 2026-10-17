package solid.ocp;

import solid.ocp.good.Discount;
import solid.ocp.good.PensionerDiscount;
import solid.ocp.good.RegularDiscount;
import solid.ocp.good.StudentDiscount;
import solid.ocp.good.VipDiscount;

/**
 * Open/Closed Principle demo.
 */
public class OcpDemo {
    public static void main(String[] args) {
        System.out.println("=== OCP: Open/Closed Principle ===");
        double price = 100;

        System.out.println("--- Before (if/else chain) ---");
        solid.ocp.bad.DiscountCalculator badCalculator = new solid.ocp.bad.DiscountCalculator();
        System.out.println("REGULAR: " + badCalculator.calculate("REGULAR", price));
        System.out.println("VIP: " + badCalculator.calculate("VIP", price));
        System.out.println("STUDENT: " + badCalculator.calculate("STUDENT", price));
        System.out.println("PENSIONER: " + badCalculator.calculate("PENSIONER", price)
                + "  <- new type not supported without modifying the class");

        System.out.println("--- After (polymorphism through the Discount interface) ---");
        solid.ocp.good.DiscountCalculator calculator = new solid.ocp.good.DiscountCalculator();
        Discount[] discounts = {
                new RegularDiscount(),
                new VipDiscount(),
                new StudentDiscount(),
                new PensionerDiscount()   // added without modifying DiscountCalculator
        };
        for (Discount discount : discounts) {
            System.out.println(discount.getClass().getSimpleName() + ": "
                    + calculator.calculate(discount, price));
        }
    }
}
