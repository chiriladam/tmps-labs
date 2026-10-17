package solid.srp.bad;

/**
 * SRP VIOLATION: the class has three different responsibilities
 * (calculation, saving, emailing), so three reasons to change.
 */
public class Invoice {
    private double amount;

    public Invoice(double amount) {
        this.amount = amount;
    }

    public double calculateTotal() {
        return amount * 1.20; // add 20% VAT
    }

    public void saveToFile() {
        // write the invoice to a file
        System.out.println("Invoice saved to invoice.txt (total: " + calculateTotal() + ")");
    }

    public void sendEmail(String email) {
        // send the invoice by email
        System.out.println("Invoice sent to " + email);
    }
}
