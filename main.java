import java.util.*;

public class main {
    public static void main(String[] args) throws Exception{
        String filePath;
        try {
            filePath = args[0];
        }
        catch (IndexOutOfBoundsException error) {
            filePath = null;
        }
        HandlePropertyFile handler = new HandlePropertyFile(filePath);
        Simulation simulation = new Simulation(handler);
        simulation.run(handler);
        simulation.report();
    }
}
