import java.util.*;
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
        Elevator currentElevator;
        Floor currentFloor;

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
            spawn(handler, floors);
            for (int i = 0; i < getElevatorCount(); i++){
                currentElevator = elevators.get(i);
                currentFloor = floors.get(currentElevator.get_CurrentFloor() - 1);//where current elevator is. needed for loading/unloading methods

                while (true) {//loops through elevator's PQ to unload all applicable passengers
                    Passenger passenger_served = currentElevator.unload();
                    if (passenger_served == null) {
                        break;
                    }
                    end_service(passenger_served);
                }

                if (currentElevator.stop(getFloorCount())) {
                    currentElevator.changeDirection();
                }

                currentElevator.load(currentFloor);

                unloadRequest = currentElevator.get_priority();
                loadRequest = button_pressed(floors, currentElevator);

                if (unloadRequest == 0) { //no passenger needs to be dropped off
                    //someone needs to be picked up
                    if (loadRequest == 0) {//no upload requests
                        currentElevator.standby(floors);
                    } else {
                        currentElevator.travel(loadRequest, floors);
                    }
                } else {//someone needs to be dropped off
                    if (loadRequest == 0) {//no pickup requests
                        currentElevator.travel(unloadRequest, floors);
                    } else {//pickup AND drop-off requests
                        int closestRequest = currentElevator.closestFloor(unloadRequest, loadRequest);
                        currentElevator.travel(closestRequest, floors);
                    }
                }
            }
            tickCount++;
        }
    }

    private int button_pressed(List<Floor> floorList, Elevator elevator) {
        int current_floor = elevator.get_CurrentFloor();
        ListIterator<Floor> iterator = floorList.listIterator(current_floor - 1);
        Floor request;

        if (elevator.direction()) {
            //look up
            while (iterator.hasNext()){
                request = iterator.next();
                current_floor++;
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

    public void spawn(HandlePropertyFile handler, List<Floor> floors) { //return if spawn was successful, 0 if no new passengers
        Random randomGenerator = new Random();
        Floor current;

        for (int floor_number = 1; floor_number <= handler.get_floors(); floor_number++) {
            current = floors.get(floor_number - 1);
            if (handler.get_passengers() >= randomGenerator.nextFloat()) {
                Passenger newPassenger = new Passenger(floor_number, handler.get_floors(), getTickCount());
                if (newPassenger.goingUp()) {
                    current.get_upload_requests(true).add(newPassenger);
                }
                else {
                    current.get_upload_requests(false).add(newPassenger);
                }
                return;
            }
        }
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
