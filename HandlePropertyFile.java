import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HandlePropertyFile {
    public Properties properties;
    public HandlePropertyFile() throws Exception {
        this.properties =  new Properties();
        this.default_setup();
        if (!valid_properties()) {
            throw new Exception("Invalid properties");
        }
    }
    public HandlePropertyFile(String file) throws Exception {
        this.properties = new Properties();
        this.default_setup();
        init_properties(file);
        if (!valid_properties()) {
            throw new Exception("Invalid properties");
        }
    }
    public void default_setup(){
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
    public void init_properties(String filePath) {
        try (FileInputStream propertyFile = new FileInputStream(filePath)) {
            properties.load(propertyFile);
        }
        catch (FileNotFoundException error) {
            //Requirement specifies that property file may be missing. Default setup should be used.
        }
        catch (IOException error){
            error.printStackTrace();
            System.exit(0);
        }
    }

    public boolean valid_properties() {
        if(getStructures().compareTo("linked") != 0 && getStructures().compareTo("array") != 0) {
            return false;
        }
        if (getFloors() < 2) {
            return false;
        }
        if (getPassengers() < 0 || getPassengers() > 1.0) {
            return false;
        }
        if (getElevators() < 1) {
            return false;
        }
        if (getElevatorCapacity() < 1) {
            return false;
        }
        if (getDuration() < 1) {
            return false;
        }
        return true;

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
