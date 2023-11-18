import java.util.*;

public class Floor {
    private int floorNumber; //index
    private List<Passenger> UpRequest;
    private List<Passenger> DownRequest;

    public Floor(HandlePropertyFile handler, int floorNumber) {
        this.floorNumber = floorNumber;

        if(handler.is_linked()) {
            UpRequest = new LinkedList<>();
            DownRequest = new LinkedList<>();
        }
        else {
            UpRequest = new ArrayList<>(handler.get_floors());
            DownRequest = new ArrayList<>(handler.get_floors());
        }
    }

    public int get_FloorNumber() {
        return floorNumber;
    }

    public List<Passenger> get_upload_requests(boolean up) {
        try {
            if (up) {
                return UpRequest;
            }
            return DownRequest;
        }
        catch (NullPointerException noPassengers) {
            return null;
        }
    }
}