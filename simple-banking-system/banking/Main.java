package banking;

/**
 * This class creates and runs a banking application
 */
public class Main {

    /**
     * The main method which creates and runs the banking application using a SQLite database
     * @param args the name of the SQLite database (e.g. cards.s3db")
     */
    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + args[1];
        Bank app = new Bank(url);
        app.run();
    }
}

