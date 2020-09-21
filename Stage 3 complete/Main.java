public class Main {

    static String FILE_NAME;

    public static void main(String[] args) {

        if (args.length > 0) {
            FILE_NAME = args[0];
        } else {
            System.out.println("Error: not found agrs!");
        }

        UserInterface userInterface = new UserInterface();
        userInterface.start();

    }
}

