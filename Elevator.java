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

    //unload() will be looped and return null if there are no more to poll in that given direction
    public Passenger unload() {
        if (get_passengerPQ().isEmpty()) {
            return null;
        }
        else if (get_priority() == get_CurrentFloor()) {
            passengerCount--;
            return get_passengerPQ().poll();
        }
        else {
            return null;
        }
    }

    public void load(Floor floor) {
        List<Passenger> passengers_waiting = floor.get_uploadRequests(direction());
        //upload all passengers that want to go the same direction (up/down)
        while (!is_full() && !passengers_waiting.isEmpty()) {//elevator has space AND there are passengers getting on
            Passenger onboard_passenger = passengers_waiting.get(0);
            //add onboarded passenger to elevator's heap
            get_passengerPQ().offer(onboard_passenger); //opted for offer() to avoid exceptions, returns false if unsuccessful
            passengers_waiting.remove(0);
            passengerCount++;
        }
    }

    //destination is the floor number, not index of that given floor in their respective list (index = destination - 1)
    public void travel(int destination, List<Floor> floors) {
        while (get_DistanceTraveled() < 5 && destination != get_CurrentFloor()) {//destination reached or max distance traveled
            if (direction()) { //elevator is going up, keep going up
                currentFloor++;
            }
            else {
                currentFloor--;
            }
            distanceTraveled++;
            if (stop(floors.size())) {//even if it can travel more, the top/bottom floor has been reached
                break;
            }
        }
        reset_distance();
    }

    //returns true if they are at the top/bottom floor and want to keep going up/down, respectively
    public boolean stop(int topFloor) {
        return (!direction() && get_CurrentFloor() == 1) || (direction() && get_CurrentFloor() == topFloor);
    }

    //travels to top/bottom floor so it can receive load requests from passengers wanting to go the opposite direction
    //called when there are no load AND no unload requests in current direction
    public void standby(List<Floor> floors) {
        if (direction()) {
            travel(floors.size(), floors);
        }
        else {
            travel(1, floors);
        }
    }

    //called when elevator has to decide where to stop first
    //parameters represent closest floors where passengers want to be picked up/dropped off
    public int closestFloor(int floor1, int floor2) {
        if(direction()) {
            return Math.min(floor1, floor2);
        }
        return Math.max(floor1, floor2);
    }
    //returns floor number of highest priority passenger
    public int get_priority() {
        if (get_passengerPQ().isEmpty()) {
            return 0;
        }
        else {
            return get_passengerPQ().peek().get_destination();
        }
    }
    private boolean is_full() {
        return passengerCount == capacity;
    }
    public boolean direction() {
        return goingUp;
    }
    //called when stop() returns true and all unloading has been completed
    //swaps the order of heap from min to/from max heap
    public void changeDirection() {
        goingUp = !direction();
        //swap PQ order
        if (direction()) {//if it's now going up, PQ needs to be a minheap
            PriorityQueue<Passenger> newPQ = new PriorityQueue<>(capacity);
            while (!get_passengerPQ().isEmpty()) {
                newPQ.offer(get_passengerPQ().poll());
            }
            unloadRequests = newPQ;
        }
        else {//if elevator going down, PQ should be maxheap
            PriorityQueue<Passenger> newPQ = new PriorityQueue<>(capacity, Collections.reverseOrder());
            while (!get_passengerPQ().isEmpty()) {
                newPQ.offer(get_passengerPQ().poll());
            }
            unloadRequests = newPQ;
        }
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