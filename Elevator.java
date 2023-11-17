import java.util.List;
import java.util.PriorityQueue;
import java.util.Collections;
public class Elevator {
    private int capacity;
    private int passenger_count;
    private int current_floor;
    private boolean going_up;
    private PriorityQueue<Passenger> QueueUp;
    private PriorityQueue<Passenger> QueueDown;
    public Elevator(HandlePropertyFile handler) {
        this.capacity = handler.getElevatorCapacity();
        this.passenger_count = 0;
        this.current_floor = 1;
        this.going_up = true;
        this.QueueUp = new PriorityQueue<>(capacity);
        this.QueueDown = new PriorityQueue<>(capacity, Collections.reverseOrder());
    }

    public void load(Floor floor) {
        List<Passenger> queue = floor.get_Q(direction());

        while (fullCapacity() && !queue.isEmpty()) {
            Passenger onboard_passenger = queue.get(0);
            if (direction()) {
                if (QueueUp.offer(onboard_passenger)) {
                    queue.remove(onboard_passenger);
                }
            }
            else {
                if (QueueDown.offer(onboard_passenger)) {
                    queue.remove(onboard_passenger);
                }
            }
        }
    }

    public Passenger unload(Floor currentFloor) {//unload() will be looped and return null if there are no more to poll in that given direction
        if (QueueUp.isEmpty() && QueueDown.isEmpty()) {
            return null;
        }
        if (direction() && !QueueUp.isEmpty()) {
            if (currentFloor.get_FloorNumber() == QueueUp.peek().get_destination()) {
                return QueueUp.poll();
            }
        }
        else {
            if (currentFloor.get_FloorNumber() == QueueDown.peek().get_destination()) {
                return QueueDown.poll();
            }
        }
        return null;
    }

    //check for requests, travel to nearest requested floor, unload, load, exit
    //easier to travel TO requested floor than travelling and checking for requests per floor
    public int travel(int destination) {
        int distance = 0;
        if (going_up) {
            while (distance < 5) {
                current_floor++;
                distance++;
            }
        }
        else {
            while (distance < 5) {
                current_floor--;
                distance++;
            }
        }
        return current_floor;
    }

    private boolean fullCapacity() {
        return passenger_count == capacity;
    }
    public boolean direction() {
        return going_up;
    }
    public int getCurrent_floor() {
        return current_floor;
    }

    //returns true if there are more travel requests for the current direction. will use bitwise operators
    private boolean check_requests() {
        //return true if there are unload requests in the current direction
        if (going_up) {
            return !QueueUp.isEmpty();
        }
        else {
            return !QueueDown.isEmpty();
        }
    }
}