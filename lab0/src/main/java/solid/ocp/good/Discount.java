package solid.ocp.good;

/**
 * The abstraction: every discount type implements this interface.
 */
public interface Discount {
    double apply(double price);
}
