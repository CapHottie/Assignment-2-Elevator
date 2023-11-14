import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HandlePropertyFile {
    public Properties properties;
    public HandlePropertyFile() {
        this.properties =  new Properties();
        default_setup(properties);
    }
    public HandlePropertyFile(String file) {
        this.properties = new Properties();
        default_setup(properties);
        properties = init_properties(file);
    }
    public void default_setup(Properties properties){
        //note for self: setProperty only accepts string arguments (even if the value is an int/float)
        properties.setProperty("structures", "linked");
        properties.setProperty("floors", "32");
        properties.setProperty("passengers", "0.03");
        properties.setProperty("elevators", "1");
        properties.setProperty("elevatorCapacity", "10");
        properties.setProperty("duration", "500");
    }

    /*Each line of the input file contains a KEY=VALUE pair
    * Input file will be passed in as args[0]*/
    public Properties init_properties(String filePath) {
        try (FileInputStream propertyFile = new FileInputStream(filePath)) {
            properties.load(propertyFile);
            return properties;
        }
        catch (FileNotFoundException error) {
            //Requirement specifies that property file may be missing. Default setup should be used.
            return properties;
        }
        catch (IOException error){
            error.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public String getStructures() {
        return properties.getProperty("structures");
    }

    public int getFloors() {
        return Integer.parseInt(properties.getProperty("floors"));
    }
    public float getPassengers() {
        return Float.parseFloat(properties.getProperty("passengers"));
    }
    public int getElevators() {
        return Integer.parseInt(properties.getProperty("elevators"));
    }
    public int getElevatorCapacity() {
        return Integer.parseInt(properties.getProperty("elevatorCapacity"));
    }
    public int getDuration() {
        return Integer.parseInt(properties.getProperty("duration"));
    }
}
