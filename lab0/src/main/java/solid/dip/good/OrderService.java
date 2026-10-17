package solid.dip.good;

/**
 * Depends only on the Database interface. The concrete implementation
 * is received through the constructor (dependency injection).
 */
public class OrderService {
    private final Database database;

    public OrderService(Database database) {  // the dependency comes from outside
        this.database = database;
    }

    public void placeOrder(String order) {
        database.save(order);
    }
}
