package solid.dip.good;

public class MongoDatabase implements Database {
    public void save(String data) {
        System.out.println("Saved to MongoDB: " + data);
    }
}
