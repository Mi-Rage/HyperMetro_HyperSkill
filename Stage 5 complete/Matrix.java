import java.util.*;

/**
 * Class of the adjacency matrix and path calculation object.
 */
public class Matrix {
    int[][] adjacency;
    LinkedList<Integer> resultSet;

    /**
     * Creating an adjacency matrix for a graph of metro lines.
     * For simple calculating of path, time from station to station is 3, the transfer is 1
     * For fasted calculating of path, time from station to station
     * get from Station obj, the transfer is 5
     *
     * @param _metroData - current metropolitan
     * @param _isFastedRoute - boolean, defines the method of calculating the time
     */
    public void generateAdjacency(HashMap<String, LinkedList<Station>> _metroData, boolean _isFastedRoute) {
        //Getting the size of the adjacency matrix
        int size = 0;
        for (String each : _metroData.keySet()) {
            size += _metroData.get(each).size() - 2;
        }
        //Create the adjacency matrix and filling her '0'
        adjacency = new int[size][size];
        for (int i = 0; i < adjacency.length; i++) {
            for (int j = 0; j < adjacency.length; j++) {
                adjacency[i][j] = 0;
            }
        }
        int index = 0;
        int adjIndex = 0;

        //If the station is not a depot - assign it
        // the global node number in the metro graph
        for (String eachLine : _metroData.keySet()) {
            LinkedList<Station> eachStationList = _metroData.get(eachLine);
            for (int i = 1; i < eachStationList.size() - 1; i++) {
                if (!eachStationList.get(i).name.equals("depot")) {
                    eachStationList.get(i).nodeNumber = index;
                    index++;
                }
            }
            _metroData.put(eachLine, eachStationList);
        }


        //Creating adjacency matrix
        for (String eachLine : _metroData.keySet()) {
            LinkedList<Station> eachStationList = _metroData.get(eachLine);

            for (int i = 1; i < eachStationList.size() - 2; i++) {
                if (!eachStationList.get(i).name.equals("depot")) {

                    if (adjIndex + 1 < adjacency.length) {
                        adjacency[adjIndex][adjIndex + 1] = _isFastedRoute ? eachStationList.get(i).time : 3;
                        adjacency[adjIndex + 1][adjIndex] = _isFastedRoute ? eachStationList.get(i).time : 3;
                        //Check whether this station intersects with something
                        if (eachStationList.get(i).transfer.size() != 0) {
                            //Getting a list of lines that intersect with the station
                            for (String eachTransferLine : eachStationList.get(i).transfer.keySet()) {
                                //Getting a list of stations that it intersects with
                                ArrayList<String> eachTransferStation = eachStationList.get(i)
                                        .transfer
                                        .get(eachTransferLine);
                                //For each intersection station, we get the index of this station to add to the matrix
                                for (String each : eachTransferStation) {
                                    LinkedList<Station> stations = _metroData.get(eachTransferLine);
                                    int transferIndex;
                                    for (Station station : stations) {
                                        if (station.name.equals(each)) {
                                            transferIndex = station.nodeNumber;
                                            adjacency[adjIndex][transferIndex] = _isFastedRoute ? 5 : 1;
                                            adjacency[transferIndex][adjIndex] = _isFastedRoute ? 5 : 1;
                                        }
                                    }
                                }
                            }
                        }
                        adjIndex++;
                    }
                }
            }
            adjIndex++;
            _metroData.put(eachLine, eachStationList);
        }

        //Remove loop in adjacency matrix
        for (int i = 0; i < adjacency.length; i++) {
            adjacency[i][i] = 0;
        }

        if (Main.DEBUG) {
            System.out.println("ADJACENSY SIZE: " + adjacency.length);
            for (int[] ints : adjacency) {
                for (int j = 0; j < adjacency.length; j++) {
                    System.out.print(ints[j] + ",");
                }
                System.out.println();
            }
        }

    }

    /**
     * Search for the shortest path using the Dijkstra algorithm
     *
     * @param _line1     - start line of metro
     * @param _station1  - start station in the start line
     * @param _line2     - finish line
     * @param _station2  - finish station
     * @param _metroData - current metro
     */
    public void findWays(String _line1, String _station1, String _line2, String _station2, HashMap<String, LinkedList<Station>> _metroData) {
        char[] known = new char[adjacency.length];
        int[] cost = new int[adjacency.length];
        int[] path = new int[adjacency.length];
        // Prepare arrays
        Arrays.fill(cost, Integer.MAX_VALUE);
        Arrays.fill(path, Integer.MAX_VALUE);

        int index = findIndexInLine(_line1, _station1, _metroData);
        if (Main.DEBUG) System.out.println("Начальный узел: " + index);
        known[index] = 'T';
        cost[index] = 0;
        path[index] = -1;
        for (int j = 0; j < adjacency.length; j++) {
            for (int i = 1; i < adjacency.length; i++) {
                if (adjacency[index][i] > 0 && known[i] != 'T') {
                    if (cost[i] > cost[index] + adjacency[index][i]) {
                        cost[i] = cost[index] + adjacency[index][i];
                        path[i] = index;
                    }
                }
            }
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < cost.length; i++) {
                if (known[i] != 'T' && cost[i] < min) {
                    min = cost[i];
                    index = i;
                }
            }
            known[index] = 'T';
        }

        if (Main.DEBUG) {
            System.out.println("Vertex  Known  Cost  Path");
            for (int i = 0; i < cost.length; i++) {
                System.out.println("  " + i + "      " + known[i] + "    " + cost[i] + "   " + path[i]);
            }
        }


        index = findIndexInLine(_line2, _station2, _metroData);
        if (Main.DEBUG) System.out.println("Конечный узел: " + index);
        resultSet = new LinkedList<>();
        resultSet.addFirst(index);
        while (path[index] != -1) {
            resultSet.addFirst(path[index]);
            index = path[index];
        }

        if (Main.DEBUG) {
            System.out.println("Путь:" + Arrays.toString(resultSet.toArray()));
            System.out.println("К-во узлов:" + resultSet.size());
        }
    }

    /**
     * Output the result of the shortest path to the console
     * @param _metroData - current metro
     */
    public void outputWay(HashMap<String, LinkedList<Station>> _metroData, boolean _isFastedRoute) {
        String[][] resultWay = new String[resultSet.size()][2];
        int totalTime = 0;

        for (int i = 0; i < resultSet.size(); i++) {
            boolean isFound = false;
            for (String eachLine : _metroData.keySet()) {
                LinkedList<Station> eachStationList = _metroData.get(eachLine);
                for (Station eachStation : eachStationList) {
                    if (eachStation.nodeNumber == resultSet.get(i) && !eachStation.name.equals("depot")) {
                        resultWay[i][0] = eachLine;
                        resultWay[i][1] = eachStation.name;
                        isFound = true;
                        break;
                    }
                }
                if (isFound) {
                    break;
                }
            }
        }

        String currentLine = resultWay[0][0];
        for (int i = 0; i < resultWay.length; i++) {
            if (i + 1 < resultWay.length) {
                if (currentLine.equals(resultWay[i + 1][0])) {
                    System.out.println(resultWay[i][1]);
                    if (i > 0) {
                        totalTime += getTimeFromIndex(findIndexInLine(resultWay[i][0], resultWay[i][1], _metroData), _metroData);
                    }
                } else {
                    System.out.println(resultWay[i][1]);
                    System.out.println("Transition to line " + resultWay[i + 1][0]);
                    totalTime += getTimeFromIndex(findIndexInLine(resultWay[i][0], resultWay[i][1], _metroData), _metroData);
                    totalTime += getTimeFromIndex(findIndexInLine(resultWay[i+1][0], resultWay[i][1], _metroData), _metroData);
                    //totalTime += 5;
                    currentLine = resultWay[i + 1][0];
                }
            } else {
                System.out.println(resultWay[i][1]);
            }
        }

        if(_isFastedRoute) {
            System.out.println("Total: " + totalTime + " minutes in the way");
        }
    }

    /**
     * Auxiliary method for searching
     * for the global node number by line and station name
     * @param _line - line of metro
     * @param _station - name station
     * @param _metroData - current metro
     * @return - int, nodeNumber this station in graph
     */
    public int findIndexInLine(String _line, String _station, HashMap<String, LinkedList<Station>> _metroData) {
        for (String eachLine : _metroData.keySet()) {
            if (eachLine.equals(_line)) {
                LinkedList<Station> eachStationList = _metroData.get(eachLine);
                for (Station eachStation : eachStationList) {
                    if (eachStation.name.equals(_station)) {
                        return eachStation.nodeNumber;
                    }
                }
            }
        }
        System.out.println("ERROR finding name station!");
        return -1;
    }

    /**
     * Auxiliary method for getting the time of a station by its global index.
     * @param _index - index of station (nodeIndex)
     * @param _metroData - current metro
     * @return - int, time of station
     */
    public int getTimeFromIndex(int _index, HashMap<String, LinkedList<Station>> _metroData) {
        boolean isFounf = false;
        for (String eachLine : _metroData.keySet()) {
                LinkedList<Station> eachStationList = _metroData.get(eachLine);
                for (Station eachStation : eachStationList) {
                    if (eachStation.nodeNumber == _index) {
                        return eachStation.time;
                    }
                }

        }
        return 0;
    }

}
