import java.util.*;
public class Elevator {
    private int capacity;
    private int passengerCount;
    private int currentFloor;
    private int distanceTraveled;
    private boolean goingUp;
    private PriorityQueue<Passenger> QueueUp;
    private PriorityQueue<Passenger> QueueDown;
    public Elevator(HandlePropertyFile handler) {
        this.capacity = handler.get_elevatorCapacity();
        this.passengerCount = 0;
        this.currentFloor = 1;
        this.goingUp = true;
        this.QueueUp = new PriorityQueue<>(capacity);
        this.QueueDown = new PriorityQueue<>(capacity, Collections.reverseOrder());
    }


    public void load(Floor floor) {
        List<Passenger> passengers_waiting = floor.get_upload_requests(direction());
        //upload all passengers that want to go the same direction (up/down)
        while (!is_full() && !passengers_waiting.isEmpty()) {//elevator has space AND there are passengers getting on
            Passenger onboard_passenger = passengers_waiting.get(0);
            get_Q().offer(onboard_passenger);
            passengers_waiting.remove(0);
            passengerCount++;
        }
    }

    public Passenger unload() {//unload() will be looped and return null if there are no more to poll in that given direction
        if (get_Q().isEmpty()) {//guaranteed to be null, saves runtime
            return null;
        }
        if (get_Q().peek().get_destination() == get_CurrentFloor()) {
            return get_Q().poll();
        }
        return null;
    }

    //returns where elevator stopped
    public int travel(int distance, List<Floor> floors) {
        while (get_DistanceTraveled() < 5 && distance > get_DistanceTraveled()) {
            if (direction()) {
                currentFloor++;
                distanceTraveled++;
                if (!floors.get(get_CurrentFloor()).get_upload_requests(true).isEmpty()) {
                    break;
                }
                else if (!floors.get(get_CurrentFloor()).get_upload_requests(false).isEmpty()) {
                    break;
                }
            }
            else {
                currentFloor--;
                distanceTraveled++;
            }
        }
        return get_CurrentFloor();
    }
    public int closestFloor(int floor1, int floor2) {
        if(direction()) {
            return Math.min(floor1, floor2);
        }
        return Math.max(floor1, floor2);
    }
    public int get_priority() {
        try {
            return get_Q().peek().get_destination();
        }
        catch (NullPointerException emptyQ) {
            return 0;
        }
    }
    private boolean is_full() {
        return passengerCount == capacity;
    }
    public boolean direction() {
        return goingUp;
    }
    public void changeDirection() {
        goingUp = !direction();
    }
    public int get_CurrentFloor() {
        return currentFloor;
    }
    public int get_DistanceTraveled() {
        return distanceTraveled;
    }
    public void reset_distance() {
        distanceTraveled = 0;
    }

    //returns true if there are more travel requests for the current direction
    public boolean check_requests() {
        //return true if there are unload requests in the current direction
        return !get_Q().isEmpty();
    }
    public PriorityQueue<Passenger> get_Q() {
        if (direction()) {
            return QueueUp;
        }
        return QueueDown;
    }
}