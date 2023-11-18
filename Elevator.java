import java.util.*;
public class Elevator {
    private int capacity;
    private int passengerCount;
    private int currentFloor;
    private int distanceTraveled;
    private boolean goingUp;
    private PriorityQueue<Passenger> unloadRequests;
    public Elevator(HandlePropertyFile handler) {
        this.capacity = handler.get_elevatorCapacity();
        this.passengerCount = 0;
        this.currentFloor = 1;
        this.goingUp = true;
        this.unloadRequests = new PriorityQueue<>(capacity);
    }


    public void load(Floor floor) {
        List<Passenger> passengers_waiting = floor.get_upload_requests(direction());
        //upload all passengers that want to go the same direction (up/down)
        while (!is_full() && !passengers_waiting.isEmpty()) {//elevator has space AND there are passengers getting on
            Passenger onboard_passenger = passengers_waiting.get(0);
            get_passengerPQ().offer(onboard_passenger);
            passengers_waiting.remove(0);
            passengerCount++;
        }
    }

    public Passenger unload() {//unload() will be looped and return null if there are no more to poll in that given direction
        if (get_passengerPQ().isEmpty()) {//guaranteed to be null, saves runtime
            return null;
        }
        if (get_passengerPQ().peek().get_destination() == get_CurrentFloor()) {
            passengerCount--;
            return get_passengerPQ().poll();
        }
        return null;
    }

    /* returns floor where elevator stopped
    *  changes direction when top/bottom has been reached
    * cannot travel more than 5 floors
    * stops when destination reached or when there is an upload queue in that floor*/
    public int travel(int destination, List<Floor> floors) {
        while (get_DistanceTraveled() < 5 || destination == get_CurrentFloor()) {
            if (direction()) {
                currentFloor++;
                distanceTraveled++;
            }
            else {
                currentFloor--;
                distanceTraveled++;
            }
            if (stop(floors.size())) {
                break;
            }
        }
        reset_distance();
        return get_CurrentFloor();
    }

    public boolean stop(int topFloor) {
        if ((!direction() && get_CurrentFloor() == 1 ) || (direction() && get_CurrentFloor() == topFloor)){
            changeDirection();
            return true;
        }
        return false;
    }

    public int standby(List<Floor> floors) {
        if (direction()) {
            return travel(floors.size(), floors);
        }
        else {
            return travel(1, floors);
        }
    }
    public int closestFloor(int floor1, int floor2) {
        if(direction()) {
            return Math.min(floor1, floor2);
        }
        return Math.max(floor1, floor2);
    }
    public int get_priority() {
        try {
            return get_passengerPQ().peek().get_destination();
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
        //swap PQ order
        PriorityQueue<Passenger> newPQ = new PriorityQueue<>(capacity, Collections.reverseOrder());
        while (!get_passengerPQ().isEmpty()) {
            newPQ.offer(get_passengerPQ().poll());
        }
        unloadRequests = newPQ;
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
    public PriorityQueue<Passenger> get_passengerPQ() {
        return unloadRequests;
    }
}