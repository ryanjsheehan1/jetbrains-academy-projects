package banking;

import java.sql.*;

/**
 * This class creates and connects to a SQLite3 database and executes SQL commands for banking actions and transactions
 */
public class Database {

    /**
     * Connects to the database
     * @param url the database path
     * @return Connection
     */
    private Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Creates a new database
     * @param url the database path
     */
    public void createNewDatabase(String url) {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates a new "cards" table.
     * The table has attributes for an id, bank account card number, PIN, and balance.
     * @param url the database path
     */
    public void createNewTable(String url) {

        // SQL statement for creating new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "id INTEGER,\n"
                + "number TEXT,\n"
                + "pin TEXT,\n"
                + "balance INTEGER DEFAULT 0"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts new records into "cards" table.
     * @param id the id number
     * @param number the bank account card number
     * @param pin the bank account card PIN
     * @param url the database path
     */
    public void insert(int id, String number, String pin, String url) {
        String sql = "INSERT INTO card(id, number, pin) VALUES(?,?,?)";

        try (Connection conn = connect(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, number);
            pstmt.setString(3, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Authenticates a user login by querying card number and PIN.
     * @param number the bank account card number
     * @param pin the bank account card PIN
     * @param url the database path
     * @return whether the login attempt was successful
     */
    public boolean login(String number, String pin, String url) {
        String sql = "SELECT number, pin FROM card";

        try (Connection conn = connect(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                if (rs.getString("number").equals(number) && rs.getString("pin").equals(pin)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Gets the balance of the bank account.
     * @param number the bank account card number
     * @param pin the bank account card PIN
     * @param url the database path
     * @return the balance of the bank account
     */
    public int getBalance(String number, String pin, String url){
        String sql = "SELECT balance FROM card WHERE number = ? AND pin = ?";
        int balance = 0;

        try (Connection conn = connect(url);
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balance;
    }

    /**
     * Adds to the balance of the bank account (deposit).
     * @param number the bank account card number
     * @param pin the bank account card PIN
     * @param url the database path
     * @param income the amount to be added to the balance
     */
    public void addIncome(String number, String pin, String url, int income) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ? AND pin = ?;";

        try (Connection conn = connect(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, income);
            pstmt.setString(2, number);
            pstmt.setString(3, pin);
            // update
            pstmt.executeUpdate();
            System.out.println("Income was added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Closes the bank account by deleting the record from the table
     * @param number the bank account card number
     * @param url the database path
     */
    public void closeAccount(String number, String url) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = connect(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, number);
            // execute the delete statement
            pstmt.executeUpdate();
            System.out.println("Your account has been closed successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Transfers funds from user's bank account balance to another bank account.
     * @param fromAccountNumber The user's bank account card number
     * @param toAccountNumber The bank account to transfer funds to
     * @param amount the funds to be transferred
     * @param url the database path
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, int amount, String url) {
        String withdrawSQL = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String depositSQL = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = connect(url)) {

            conn.setAutoCommit(false);

            try (PreparedStatement withdraw = conn.prepareStatement(withdrawSQL)) {

                withdraw.setInt(1, amount);
                withdraw.setString(2, fromAccountNumber);
                withdraw.executeUpdate();

                PreparedStatement deposit = conn.prepareStatement(depositSQL);

                deposit.setInt(1, amount);
                deposit.setString(2, toAccountNumber);
                deposit.executeUpdate();



                conn.commit();
                System.out.println("Success!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks whether a bank account card number exists.
     * @param toCardNumber the bank account card number to be checked
     * @param url the database path
     * @return whether a bank account card number exists
     */
    public boolean cardExist(String toCardNumber, String url) {
        String sql = "SELECT * FROM card WHERE number = ?";
        String number = "";

        try (Connection conn = connect(url);
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setString(1,toCardNumber);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                number = rs.getString("number");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return !number.equals("");
    }
}
