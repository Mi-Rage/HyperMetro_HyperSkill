import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * User interface. All commands and their results are here
 */
public class UserInterface {
    Scanner scanner = new Scanner(System.in);
    HashMap<String, LinkedList<Station>> metroData = new HashMap<>();
    String[] command;
    Matrix matrix = new Matrix();

    public void start() {

        InfoReader infoReader = new InfoReader();
        metroData = infoReader.readFile(Main.FILE_NAME, metroData);

        getCommand();

        while (!command[0].equals("/exit")) {
            switch (command[0]) {
                case ("/append"):
                    if (command[1] != null && command[2] != null) {
                        appendStation(metroData, command[1], command[2]);
                    } else {
                        System.out.println("Invalid command");
                    }
                    break;
                case ("/add-head"):
                    if (command[1] != null && command[2] != null) {
                        addStation(metroData, command[1], command[2]);
                    } else {
                        System.out.println("Invalid command");
                    }
                    break;
                case ("/remove"):
                    if (command[1] != null && command[2] != null) {
                        removeStation(metroData, command[1], command[2]);
                    } else {
                        System.out.println("Invalid command");
                    }
                    break;
                case ("/output"):
                    if (command[1] != null) {
                        printLineStations(metroData, command[1]);
                    } else {
                        System.out.println("Invalid command");
                    }
                    break;
                case ("/connect"):
                    connectStations(metroData, command[1], command[2], command[3], command[4]);
                    break;
                case ("/route"):
                    matrix.generateAdjacency(metroData, false);
                    matrix.findWays(command[1], command[2], command[3], command[4], metroData);
                    matrix.outputWay(metroData, false);
                    break;
                case ("/fastest-route"):
                    matrix.generateAdjacency(metroData, true);
                    matrix.findWays(command[1], command[2], command[3], command[4], metroData);
                    matrix.outputWay(metroData, true);
                    break;
                default:
                    System.out.println("Invalid command");
                    break;

            }
            getCommand();
        }
    }

    /**
     * Convert a string with commands to the desired format.
     * Remove the quotation marks and highlight the arguments.
     * I did what I could with crutches. ))
     */
    public void getCommand() {

        String[] inputLine = scanner.nextLine().split(" ");
        command = new String[5];
        int indInCom = 0;
        int indInLine = 0;

        while (indInLine < inputLine.length) {
            if (inputLine[indInLine].startsWith("\"")) {
                command[indInCom] = inputLine[indInLine];
                if (indInCom == inputLine.length - 1) {
                    command[indInCom] = command[indInCom].replaceAll("\"", "");
                    return;
                }
                if (!inputLine[indInLine].endsWith("\"")) {
                    for (int j = indInLine + 1; j < inputLine.length; j++) {
                        if (inputLine[j].endsWith("\"")) {
                            command[indInCom] += " " + inputLine[j];
                            command[indInCom] = command[indInCom].replaceAll("\"", "");
                            indInCom++;
                            indInLine = j + 1;
                            break;
                        } else {
                            command[indInCom] += " " + inputLine[j];
                        }
                    }
                } else {
                    command[indInCom] = command[indInCom].replaceAll("\"", "");
                    indInLine++;
                    indInCom++;
                }
            } else if (!inputLine[indInLine].endsWith("\"")) {
                command[indInCom] = inputLine[indInLine];
                indInCom++;
                indInLine++;
            } else {
                indInLine++;
            }
        }
    }

    /**
     * Adding this station to the end of the selected branch
     *
     * @param _metroData   - collection of branches and a list of their stations
     * @param _lineName    - selected branch
     * @param _stationName - selected station
     */
    public void appendStation(HashMap<String, LinkedList<Station>> _metroData, String _lineName, String _stationName) {
        if (_metroData.containsKey(_lineName)) {
            LinkedList<Station> stations = _metroData.get(_lineName);
            stations.add(stations.size() - 1, new Station(_stationName));
            _metroData.put(_lineName, stations);
        } else {
            System.out.println("Error: invalid line name");
        }
    }

    /**
     * Adding this station to the beginning of the selected branch
     *
     * @param _metroData   - collection of branches and a list of their stations
     * @param _lineName    - selected branch
     * @param _stationName - selected station
     */
    public void addStation(HashMap<String, LinkedList<Station>> _metroData, String _lineName, String _stationName) {
        if (_metroData.containsKey(_lineName)) {
            LinkedList<Station> stations = _metroData.get(_lineName);
            stations.add(1, new Station(_stationName));
            _metroData.put(_lineName, stations);
        } else {
            System.out.println("Error: invalid line name");
        }
    }

    /**
     * Remove this station of the selected branch
     *
     * @param _metroData   - collection of branches and a list of their stations
     * @param _lineName    - selected branch
     * @param _stationName - selected station
     */
    public void removeStation(HashMap<String, LinkedList<Station>> _metroData, String _lineName, String _stationName) {
        if (_metroData.containsKey(_lineName)) {
            LinkedList<Station> stations = _metroData.get(_lineName);
            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).name.equals(_stationName)) {
                    stations.remove(i);
                    break;
                }
            }
            _metroData.put(_lineName, stations);
        } else {
            System.out.println("Error: invalid line name");
        }
    }

    /**
     * Output selected branch in the required format
     *
     * @param _metroData - collection of branches and a list of their stations
     * @param _lineName  - selected branch
     */
    public void printLineStations(HashMap<String, LinkedList<Station>> _metroData, String _lineName) {
        if (_metroData.containsKey(_lineName)) {
            LinkedList<Station> stations = _metroData.get(_lineName);

            for (int i = 2; i < stations.size(); i++) {
                System.out.printf("%s - %s - %s\n", getStationOnOutput(stations.get(i - 2))
                                                  , getStationOnOutput(stations.get(i - 1))
                                                  , getStationOnOutput(stations.get(i)));
            }
        } else {
            System.out.println("Error: invalid line name");
        }
    }

    /**
     * Auxiliary method for generating station names
     * with (without) transfers for output to the console
     * @param _station - current station for output
     * @return - String with name station with (without) line of transfer
     */
    public String getStationOnOutput (Station _station){
        if (_station.transfer.isEmpty()) {
            return _station.name;
        } else {
            String transfer = "";
            for (String lines : _station.transfer.keySet()) {
                for (String station : _station.transfer.get(lines)) {
                    transfer = " - " + station + " (" + lines + " lines)";
                }
            }
            return _station.name + transfer;
        }
    }

    /**
     * Connecting stations in branches!
     *
     * @param _metroData    - collection of branches and a list of their stations
     * @param _line1        - branch 1
     * @param _stationName1 - name of station1 from branch1
     * @param _line2        - branch 1
     * @param _stationName2 - name of station2 from branch2
     */
    public void connectStations(HashMap<String, LinkedList<Station>> _metroData, String _line1, String _stationName1,
                                String _line2, String _stationName2) {

        LinkedList<Station> lineStations1 = _metroData.get(_line1);
        LinkedList<Station> lineStations2 = _metroData.get(_line2);
        for (Station eachStation : lineStations1) {
            if (eachStation.name.equals(_stationName1)) {
                HashMap<String, ArrayList<String>> newTransfer = eachStation.transfer;
                ArrayList<String> currentTransfer;
                if (newTransfer.containsKey(_line2)) {
                    currentTransfer = newTransfer.get(_line2);
                } else {
                    currentTransfer = new ArrayList<>();
                }
                currentTransfer.add(_stationName2);
                newTransfer.put(_line2, currentTransfer);
                eachStation.transfer = newTransfer;
            }
        }

        for (Station eachStation : lineStations2) {
            if (eachStation.name.equals(_stationName2)) {
                HashMap<String, ArrayList<String>> newTransfer = eachStation.transfer;
                ArrayList<String> currentTransfer;
                if (newTransfer.containsKey(_line1)) {
                    currentTransfer = newTransfer.get(_line1);
                } else {
                    currentTransfer = new ArrayList<>();
                }
                currentTransfer.add(_stationName1);
                newTransfer.put(_line1, currentTransfer);
                eachStation.transfer = newTransfer;
            }
        }
    }
}

