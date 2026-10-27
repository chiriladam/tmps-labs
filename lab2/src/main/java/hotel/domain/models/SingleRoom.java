package hotel.domain.models;

import java.util.List;

public class SingleRoom extends Room {
    public SingleRoom(int number) {
        super(number, RoomType.SINGLE, 60, 1, List.of("Wi-Fi", "Work desk"));
    }
}
