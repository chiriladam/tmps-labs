package hotel.domain.models;

import java.util.List;

/**
 * Base class for every room in the hotel.
 * Concrete rooms are created only through the room factories (Factory Method).
 */
public abstract class Room {
    private final int number;
    private final RoomType type;
    private final int pricePerNight;
    private final int capacity;
    private final List<String> amenities;

    protected Room(int number, RoomType type, int pricePerNight, int capacity, List<String> amenities) {
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.amenities = List.copyOf(amenities);
    }

    public int getNumber() { return number; }
    public RoomType getType() { return type; }
    public int getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public List<String> getAmenities() { return amenities; }

    @Override
    public String toString() {
        return String.format("Room %d (%s) - %d EUR/night, up to %d guest(s), %s",
                number, type, pricePerNight, capacity, String.join(", ", amenities));
    }
}
