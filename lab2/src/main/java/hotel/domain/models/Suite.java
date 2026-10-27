package hotel.domain.models;

import java.util.List;

public class Suite extends Room {
    public Suite(int number) {
        super(number, RoomType.SUITE, 180, 4, List.of("Wi-Fi", "King bed", "Living room", "Jacuzzi"));
    }
}
