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
        List<Floor> floors;

        if (handler.getStructures().compareTo("linked") == 0) { //linked lists
            floors = new LinkedList<>();
        }
        else {
            floors = new ArrayList<>(handler.getFloors());
        }



    }
}
