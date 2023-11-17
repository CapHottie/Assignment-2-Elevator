import java.util.*;
public class Passenger {
    private int destination;
    private boolean goingUp;
    private int startTime;

    public Passenger(int current_floor, int max_floor, int tick_count) {
        this.startTime = tick_count;
        Random random_number = new Random();

        if (current_floor == max_floor) {
            this.goingUp = false;
        }
        else if (current_floor == 1) {
            this.goingUp = true;
        }
        else {
            this.goingUp = random_number.nextBoolean();
        }
        int range;
        if (goingUp) {
            range = (max_floor - current_floor + 1) + 1;
            this.destination = current_floor + 1 +(int)(range * Math.random()) ;
        }
        else {
            range = current_floor + 1;
            this.destination = 1 + (int)(range * Math.random());
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
}
