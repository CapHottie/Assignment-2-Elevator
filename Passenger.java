import java.util.*;
public class Passenger implements Comparable<Passenger> {
    private int destination;
    private boolean goingUp;
    private int startTime;

    public Passenger(int currentFloor, int maxFloor, int tickCount) {
        this.startTime = tickCount;
        Random randomNumber = new Random();

        if (currentFloor == maxFloor) {
            this.goingUp = false;
        }
        else if (currentFloor == 1) {
            this.goingUp = true;
        }
        else {
            this.goingUp = randomNumber.nextBoolean();
        }
        if (goingUp) {
            this.destination = randomNumber.nextInt(maxFloor - currentFloor) + currentFloor + 1;
        }
        else {
            this.destination = randomNumber.nextInt(currentFloor - 1) + 1;
        }
    }

    public int get_startTime() {
        return startTime;
    }
    public int get_destination() {
        return destination;
    }

    public boolean goingUp() {
        return goingUp;
    }

    @Override
    public int compareTo(Passenger o) {
        return Integer.compare(this.destination, o.destination);
    }
}
