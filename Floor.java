import java.util.*;

public class Floor {
    private int floorNumber;
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

    //returns a list of passengers who are waiting to get picked up by elevator
    // IFF passengers' direction of request is the same as the elevator's (represented by "up")
    public List<Passenger> get_uploadRequests(boolean up) {
        if (up) {
            return UpRequest;
        }
        return DownRequest;
    }
}