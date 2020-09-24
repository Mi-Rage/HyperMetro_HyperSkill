public class Main {

    static String FILE_NAME;
    static boolean DEBUG = false;

    public static void main(String[] args) {

        if (args.length > 0) {
            FILE_NAME = args[0];
        } else {
            System.out.println("ERROR: args not found!");
        }

        UserInterface userInterface = new UserInterface();
        userInterface.start();

    }
}

