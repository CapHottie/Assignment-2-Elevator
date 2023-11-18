import java.util.*;
import java.lang.Math;
public class Simulation {
    private int tickCount;

    private int elevatorCount;

    private int floorCount;
    private int duration;
    private int longestService;
    private int shortestService;
    private List<Integer> serviceRecord;
    public Simulation(HandlePropertyFile handler) {
        this.tickCount = 0;
        this.longestService = Integer.MIN_VALUE;
        this.shortestService = Integer.MAX_VALUE;
        if (handler.is_linked()) {
            this.serviceRecord = new LinkedList<>();
        }
        else {
            this.serviceRecord = new ArrayList<>();
        }
        this.elevatorCount = handler.get_elevators();
        this.floorCount = handler.get_floors();
        this.duration = handler.get_duration();
    }

    public void run(HandlePropertyFile handler) {
        List<Floor> floors = generate_floors(handler);
        List<Elevator> elevators = generate_elevators(handler);

        ListIterator<Elevator> elevatorIterator = elevators.listIterator(0);
        Elevator currentElevator = elevators.get(0);
        Floor currentFloor = floors.get(currentElevator.get_CurrentFloor());

        /* 1.unload in current floor
         * 2.load in current floor
         * 3.check highest priority destination in current direction
         * 4.check closest button pressed
         * 5.travel to whichever is closest.
         *   5a. if both are empty/null/0, change direction and go back to (1)
         *   5b. if PQ in current direction is empty, default to button pressed request
         *   5c. if there are no floor pressed button, default to unload request*/
        int unloadRequest;
        int loadRequest;

        while (getTickCount() <= getDuration()) {

            while (currentElevator != null && spawn(handler, floorsDELETE)){//triggers if a passenger spawned
                while (true) {//loops through elevator's PQ to unload all applicable passengers
                    Passenger passenger_served = currentElevator.unload();
                    if (passenger_served == null) {
                        break;
                    }
                    end_service(passenger_served);
                }

                currentElevator.load(currentFloor);

                if (!currentElevator.check_requests() && button_pressed(floors, currentElevator) == 0) {
                    currentElevator.changeDirection();
                    continue;
                }

                unloadRequest = currentElevator.get_priority();

                if (unloadRequest != 0) {
                    currentFloor = floors.get(currentElevator.travel(unloadRequest, floors));
                }


                loadRequest = button_pressed(floors, currentElevator);//

                if (unloadRequest != 0) {//there is a request from a passenger to be dropped off on a floor in the current direction
                    if(loadRequest != 0) {//there is a request from a passenger on a floor to be picked up by this elevator (due to its current direction)
                        currentFloor = floors.get(currentElevator.travel(currentElevator.closestFloor(loadRequest, unloadRequest), ));
                    }
                    else {//nobody needs to be picked up
                        currentFloor = floors.get(currentElevator.travel(unloadRequest, ));
                    }
                }
                else if (loadRequest != 0) {//elevator only has to pick someone up
                    currentFloor = floors.get(currentElevator.travel(loadRequest, ));
                }
                else { //no outstanding requests. elevator waits for requests and changes direction to see if there are missing requests in that direction
                    currentElevator.changeDirection();
                }


                try {//elevator has passenger, there may or may not be load requests
                    unloadRequest = currentElevator.get_priority();
                    loadRequest = button_pressed(floors, currentElevator);

                    if (loadRequest == 0) { //no load requests
                        currentFloor = floors.get(currentElevator.travel(Math.abs(unloadRequest - currentFloor.get_FloorNumber()), ));
                    } else {
                        currentFloor = floors.get(currentElevator.travel(Math.abs(currentElevator.closestFloor(unloadRequest, loadRequest) - currentFloor.get_FloorNumber()), ));
                    }
                } catch (NullPointerException noPassengers) {//no passengers in elevator that need to travel in the current direction AND someone pressed the button
                    loadRequest = button_pressed(floors, currentElevator);
                    currentFloor = floors.get(currentElevator.travel(Math.abs(loadRequest - currentFloor.get_FloorNumber()), ));
                }
                currentElevator = elevatorIterator.next();
                currentFloor = floors.get(currentElevator.get_CurrentFloor());
            }
            tickCount++;
        }
    }

    private int button_pressed(List<Floor> floorList, Elevator elevator) {
        int current_floor = elevator.get_CurrentFloor();
        ListIterator<Floor> iterator = floorList.listIterator(current_floor);
        Floor request;

        if (elevator.direction()) {
            //look up
            while (iterator.hasNext()){
                request = iterator.next();
                if (!request.get_upload_requests(elevator.direction()).isEmpty()) {
                    return request.get_FloorNumber();
                }
            }
        }
        else {
            while (iterator.hasPrevious()){
                request = iterator.previous();
                if (!request.get_upload_requests(elevator.direction()).isEmpty()) {
                    return request.get_FloorNumber();
                }
            }
        }
        return 0;
    }

    public boolean spawn(HandlePropertyFile handler, List<Floor> floors) { //return if spawn was successful, 0 if no new passengers
        Random randomGenerator = new Random();
        ListIterator<Floor> iterator = floors.listIterator();
        Floor current = floors.get(0);

        for (int floor_number = 1; floor_number <= handler.get_floors(); floor_number++) {
            if (handler.get_passengers() >= randomGenerator.nextFloat()) {
                Passenger newPassenger = new Passenger(floor_number, handler.get_floors(), getTickCount());
                if (newPassenger.goingUp()) {
                    current.get_upload_requests(true).add(newPassenger);
                }
                else {
                    current.get_upload_requests(false).add(newPassenger);
                }
                return true;
            }
            current = iterator.next();
        }
        return false;
    }

    public List<Floor> generate_floors(HandlePropertyFile handler) {
        List<Floor> floors;
        if (handler.is_linked()) {
            floors = new LinkedList<>();
        }
        else {
            floors = new ArrayList<>(getFloorCount());
        }
        for (int i = 0; i < getFloorCount(); i++) {
            Floor newFloor = new Floor(handler, i + 1);
            floors.add(newFloor);
        }
        return floors;
    }

    public List<Elevator> generate_elevators(HandlePropertyFile handler) {
        List<Elevator> elevators;

        if (handler.is_linked()) {
            elevators = new LinkedList<>();
        }
        else {
            elevators = new ArrayList<>(getElevatorCount());
        }
        for (int i = 0; i < getElevatorCount(); i++) {
            Elevator newElevator = new Elevator(handler);
            elevators.add(newElevator);
        }
        return elevators;
    }

    public void end_service(Passenger passenger_served) {//passenger is already polled from elevator PQ
        int service_length = getTickCount() - passenger_served.get_startTime();
        serviceRecord.add(service_length);
        if (service_length > longestService) {
            longestService = service_length;
        }
        if (service_length < shortestService) {
            shortestService = service_length;
        }
        passenger_served = null;
    }

    /* check if there are buttons pressed across all floors in the current direction
     *  return the closes floor who has a passenger that needs to be picked up
     *  returns 0 by default if no requests*/
    private double average_service_length() {
        double denominator = serviceRecord.size();
        double sum = 0;
        while (!serviceRecord.isEmpty()) {
            sum += serviceRecord.get(0);
            serviceRecord.remove(0);
        }
        return sum / denominator;
    }

    public void report() {
        System.out.println("Average travel time: " + average_service_length() + " ticks");
        System.out.println("Longest travel time: " + longestService + " ticks");
        System.out.println("Shortest travel time: " + shortestService + " ticks");
    }

    public int getTickCount() {
        return tickCount;
    }

    public int getElevatorCount() {
        return elevatorCount;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public int getDuration() {
        return duration;
    }
}
