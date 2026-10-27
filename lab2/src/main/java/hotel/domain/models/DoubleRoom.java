package hotel.domain.models;

import java.util.List;

public class DoubleRoom extends Room {
    public DoubleRoom(int number) {
        super(number, RoomType.DOUBLE, 90, 2, List.of("Wi-Fi", "Queen bed", "City view"));
    }
}
