import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Reading data from a file in JSON format
 * and converting it to a list of branches and stations
 */
public class InfoReader {

    /**
     * Reading JSON data from a file
     * @param _fileName - String, argument from Main.class
     * @param _metroData - HashMap, collection of branches and a list of their stations
     * @return - HashMap, updated collection of branches and a list of their stations
     */
    public HashMap<String, LinkedList<String>> readFile(String _fileName, HashMap<String, LinkedList<String>> _metroData){

        File file = new File(_fileName);
        StringBuilder result = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String readLine = scanner.nextLine();
                result.append(readLine).append("\n");
            }
        } catch (
                FileNotFoundException e) {
            System.out.println("Error: No file found: " + _fileName);
            System.exit(0);
        }

        jsonConverter(result.toString(), _metroData);
        return _metroData;
    }

    /**
     * Сonverting it to a list of branches and stations
     * @param _json_string - String, all data from the JSON file in one string
     * @param _metroData - HashMap, updated collection of branches and a list of their stations
     */
    public void jsonConverter(String _json_string, HashMap<String, LinkedList<String>> _metroData) {
        JsonObject elem = JsonParser.parseString(_json_string).getAsJsonObject();
        String[] lineName = elem.keySet().toString().replaceAll("[\\[\\]]", "").split(", ");
        for (String eachLine : lineName) {

            LinkedList<String> lineWay = new LinkedList<>();

            JsonObject categories = elem.getAsJsonObject(eachLine);
            int index = 1;
            while (true) {
                JsonElement item = categories.get(String.valueOf(index));
                if (item == null) {
                    break;
                }
                String stationName = item.toString().replaceAll("\"", "");
                lineWay.add(stationName);
                index++;
            }
            lineWay.addFirst("depot");
            lineWay.addLast("depot");
            _metroData.put(eachLine, lineWay);
        }
    }
}
