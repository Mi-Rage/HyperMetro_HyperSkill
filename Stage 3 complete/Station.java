import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object class of each station. With fields for name, time (for the future),
 * and a map with an array of stations that intersect with other lines
 */
public class Station {
    String name;
    int time;
    HashMap<String, ArrayList<String>> transfer = new HashMap<>();

    public Station(){
    }

    public Station(String _name){
        this.name = _name;
    }


}
