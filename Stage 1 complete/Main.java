import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        LinkedList<String> stationList = new LinkedList<>();

        String pathToFile = args[0];
        File file = new File(pathToFile);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String readLine = scanner.nextLine();
                stationList.add(readLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: No file found: " + pathToFile);
        }

        if (stationList.size() == 0) {
            return;
        }

        stationList.addFirst("depot");
        stationList.addLast("depot");

        for (int i = 2; i < stationList.size(); i++) {
            System.out.printf("%s - %s - %s\n", stationList.get(i - 2), stationList.get(i - 1), stationList.get(i));
        }
    }
}

