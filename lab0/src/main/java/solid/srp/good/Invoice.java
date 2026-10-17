package solid.srp.good;

/**
 * Single responsibility: invoice data and total calculation.
 */
public class Invoice {
    private double amount;

    public Invoice(double amount) {
        this.amount = amount;
    }

    public double calculateTotal() {
        return amount * 1.20; // add 20% VAT
    }
}
