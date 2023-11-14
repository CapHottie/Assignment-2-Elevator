public class Simulation {
    public static void main(String[] args) throws Exception{
        try {
            HandlePropertyFile handler = new HandlePropertyFile(args[0]);
            System.out.println(handler.getStructures());
        }
        catch (IndexOutOfBoundsException error) {
            HandlePropertyFile handler = new HandlePropertyFile();
            System.out.println(handler.getStructures());
        }
    }
}
