import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HandlePropertyFile {
    private Properties properties;

    public HandlePropertyFile(String file) throws Exception {
        this.properties = new Properties();
        this.default_setup();
        if (file != null) {
            init_properties(file);
            if (!valid_properties()) {
                throw new Exception("Invalid properties");
            }
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
            if(get_structures().compareTo("linked") != 0 && get_structures().compareTo("array") != 0) {
            return false;
        }
        if (get_floors() < 2) {
            return false;
        }
        if (get_passengers() < 0 || get_passengers() > 1.0) {
            return false;
        }
        if (get_elevators() < 1) {
            return false;
        }
        if (get_elevatorCapacity() < 1) {
            return false;
        }
        if (get_duration() < 1) {
            return false;
        }
        return true;

    }

    public Properties get_properties() {
        return properties;
    }

    public String get_structures() {
        return properties.getProperty("structures");
    }
    public boolean is_linked() {
        return get_structures().compareTo("linked") == 0;
    }
    public int get_floors() {
        return Integer.parseInt(properties.getProperty("floors"));
    }
    public float get_passengers() {
        return Float.parseFloat(properties.getProperty("passengers"));
    }
    public int get_elevators() {
        return Integer.parseInt(properties.getProperty("elevators"));
    }
    public int get_elevatorCapacity() {
        return Integer.parseInt(properties.getProperty("elevatorCapacity"));
    }
    public int get_duration() {
        return Integer.parseInt(properties.getProperty("duration"));
    }
}
