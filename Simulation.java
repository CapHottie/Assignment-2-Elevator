import java.util.*;
public class Simulation {
    private int global_tick;
    private int elevatorCount;
    private int floorCount;
    private int duration;
    private int longest_service;
    private int shortest_service;
    private List<Integer> service_record;

    public Simulation(HandlePropertyFile handler) {
        this.global_tick = 0;
        this.longest_service = Integer.MIN_VALUE;
        this.shortest_service = Integer.MAX_VALUE;
        if (handler.isLinked()) {
            this.service_record = new LinkedList<>();
        }
        else {
            this.service_record = new ArrayList<>();
        }
        this.elevatorCount = handler.getElevators();
        this.floorCount = handler.getFloors();
        this.duration = handler.getDuration();
    }

    public void run(HandlePropertyFile handler) {
        List<Floor> floors = generate_floors(handler);
        List<Elevator> elevators = generate_elevators(handler);

        Iterator<Floor> floorIterator = floors.listIterator(0);
        Iterator<Elevator> elevatorIterator = elevators.listIterator(0);
        Elevator current_elevator = elevators.get(0);
        Floor current_floor = floors.get(0);

        while (global_tick <= duration) {
            spawn(handler, floors);

            while (true) {
                Passenger passenger_served = current_elevator.unload(current_floor);
                if (passenger_served != null) {
                    break;
                }
                end_service(passenger_served);
            }

            current_elevator.load(current_floor);

            global_tick++;
        }
    }

    /* check if there are buttons pressed across all floors in the current direction
    *  return the closes floor who has a passenger that needs to be picked up */
    private int button_pressed(List<Floor> floorList, Elevator elevator) {
        int current_floor = elevator.getCurrent_floor();
        Iterator<Floor> iterator = floorList.listIterator(current_floor);
        Floor request = floorList.get(current_floor);

        if (elevator.direction()) {
            //look up
            while (iterator.hasNext()){
                request = iterator.next();
            }
        }
    }

    public void spawn(HandlePropertyFile properties, List<Floor> floors) {
        Random random_generator = new Random();
        Iterator<Floor> iterator = floors.iterator();
        Floor current = floors.get(0);
        int floor_number = 1;

        while(iterator.hasNext()) {
            if (properties.getPassengers() < random_generator.nextFloat()) {
                Passenger newPassenger = new Passenger(floor_number, floorCount, global_tick);
                if (newPassenger.goingUp()) {
                    current.get_Q(true).add(newPassenger);
                }
                else {
                    current.get_Q(false).add(newPassenger);
                }
                current = iterator.next();
                floor_number++;
            }
        }
        //current is on the top floor
        if (properties.getPassengers() < random_generator.nextFloat()) {
            Passenger newPassenger = new Passenger(floor_number, floorCount, global_tick);
            //has to go down
            current.get_Q(false).add(newPassenger);
        }
    }

    public List<Floor> generate_floors(HandlePropertyFile handler) {
        List<Floor> floors;
        if (handler.isLinked()) {
            floors = new LinkedList<>();
        }
        else {
            floors = new ArrayList<>(floorCount);
        }
        for (int i = 0; i < floorCount; i++) {
            Floor newFloor = new Floor(handler, i + 1);
            floors.add(newFloor);
        }
        return floors;
    }
    
    public List<Elevator> generate_elevators(HandlePropertyFile handler) {
        List<Elevator> elevators;
        if (handler.isLinked()) {
            elevators = new LinkedList<Elevator>();
        }
        else {
            elevators = new ArrayList<>(elevatorCount);
        }
        for (int i = 0; i < elevatorCount; i++) {
            Elevator newElevator = new Elevator(handler);
            elevators.add(newElevator);
        }
        return elevators;
    }

    public void end_service(Passenger passenger_served) {//passenger is already polled from elevator PQ
        Integer service_length = global_tick - passenger_served.get_startTime();
        service_record.add(service_length);
        if (service_length > longest_service) {
            longest_service = service_length;
        }
        if (service_length < shortest_service) {
            shortest_service = service_length;
        }
        passenger_served = null;
    }

    private double average_service_length() {
        double denominator = service_record.size();
        double sum = 0;
        while (!service_record.isEmpty()) {
            sum += service_record.get(0);
            service_record.remove(0);
        }
        return sum / denominator;
    }

    public void report() {
        System.out.println("Average travel time: " + average_service_length() + " ticks");
        System.out.println("Longest travel time: " + longest_service + " ticks");
        System.out.println("Shortest travel time: " + shortest_service + " ticks");
    }
}
