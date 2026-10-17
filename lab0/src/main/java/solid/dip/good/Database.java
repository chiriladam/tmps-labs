package solid.dip.good;

/**
 * The abstraction that both OrderService and the concrete implementations depend on.
 */
public interface Database {
    void save(String data);
}
