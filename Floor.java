import java.util.*;

public class Floor {
    private int floorNumber; //index
    private List<Passenger> UpRequest;
    private List<Passenger> DownRequest;

    public Floor(HandlePropertyFile handler, int floor_number) {
        this.floorNumber = floor_number;

        if(handler.isLinked()) {
            UpRequest = new LinkedList<>();
            DownRequest = new LinkedList<>();
        }
        else {
            UpRequest = new ArrayList<>();
            DownRequest = new ArrayList<>();
        }
    }

    public int get_FloorNumber() {
        return floorNumber;
    }

    public List<Passenger> get_Q(boolean up) {
        if (up) {
            return UpRequest;
        }
        return DownRequest;
    }
}