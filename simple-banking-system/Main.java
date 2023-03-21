package banking;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + args[1];
        createNewDatabase(url);
        createNewTable(url);
        Main app = new Main();
        int id = 0;

        outerloop:
        while(true) {
            System.out.println("\n1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner sc = new Scanner(System.in);

            System.out.print(">");
            int input = sc.nextInt();

            if (input == 1) {
                id++;
                String number  = AccountGenerator.generateCardNumber();
                String pin = AccountGenerator.generatePin();
                app.insert(id,number, pin, url);
                System.out.println("\nYour card number has been created");
                System.out.println("Your card number:\n" + number);
                System.out.println("Your card PIN:\n" + pin);
            }

            if (input == 2) {
                System.out.print("\nEnter your card number:\n>");
                String number = sc.next();
                System.out.print("Enter you PIN:\n>");
                String pin = sc.next();

                if (app.login(number, pin, url)) {
                    System.out.println("\nYou have successfully logged in!");
                    innerloop:
                    while(true) {
                        System.out.println("\n1. Balance");
                        System.out.println("2. Add income");
                        System.out.println("3. Do transfer");
                        System.out.println("4. Close account");
                        System.out.println("5. Log out");
                        System.out.println("0. Exit");
                        int input2 = sc.nextInt();
                        switch (input2) {
                            case 1:
                                System.out.println("\nBalance: " + app.getBalance(number, pin, url));
                                break;

                            case 2:
                                System.out.println("\nEnter income:");
                                int income  = sc.nextInt();
                                app.addIncome(number, pin, url, income);
                                break;

                            case 3:
                                System.out.println("\nTransfer\nEnter card number:");
                                String toAccountNumber = sc.next();

                                if (!checkLuhn(toAccountNumber)) {
                                    System.out.println("Probably you made a mistake in the card number. " +
                                            "Please try again!");
                                    break;
                                }

                                if(!app.cardExist(toAccountNumber, url)) {
                                    System.out.println("Such a card does not exist.");
                                    break;
                                }

                                System.out.println("Enter how much money you want to transfer:");
                                int amount = sc.nextInt();

                                if (number.equals(toAccountNumber)) {
                                    System.out.println("You can't transfer money to the same account!");
                                    break;
                                }

                                if (amount > app.getBalance(number, pin, url)) {
                                    System.out.println("Not enough money!");
                                    break;
                                }

                                app.transfer(number, toAccountNumber, amount, url);
                                break;

                            case 4:
//                                System.out.println("Are you sure you want to close your account? (y/N)");
//                                String input3 = sc.next();
//                                if ("Y".equals(input3) || "y".equals(input3)) {
//                                    System.out.println("Please enter card number: ");
//                                    String enteredNumber = sc.next();
//                                    System.out.println("Please enter card PIN: ");
//                                    String enteredPin = sc.next();
//                                    if (number.equals(enteredNumber) && pin.equals(enteredPin)) {
//                                        app.closeAccount(number, url);
//                                    }
//                                }
                                app.closeAccount(number, url);
                                System.out.println("\nThe account has been closed!");
                                break innerloop;

                            case 0:
                                System.out.println("Bye!");
                                break outerloop;
                        }
                    }
                } else {
                    System.out.println("\nWrong card number or PIN!");
                }
            }

            if (input == 0) {
                System.out.println("\nBye!");
                break;
            }
        }
    }

    private Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase(String url) {

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

    public static void createNewTable(String url) {

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

    public void insert(int id, String number, String pin, String url) {
        String sql = "INSERT INTO card(id, number, pin) VALUES(?,?,?)";

        try (Connection conn = this.connect(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, number);
                    pstmt.setString(3, pin);
                    pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean login(String number, String pin, String url) {
        String sql = "SELECT number, pin FROM card";

        try (Connection conn = this.connect(url);
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

    public int getBalance(String number, String pin, String url){
        String sql = "SELECT balance FROM card WHERE number = ? AND pin = ?";
        int balance = 0;

        try (Connection conn = this.connect(url);
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

    public void addIncome(String number, String pin, String url, int income) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ? AND pin = ?;";

        try (Connection conn = this.connect(url);
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

    public void closeAccount(String number, String url) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = this.connect(url);
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

    public void transfer(String fromAccountNumber, String toAccountNumber, int amount, String url) {
        String withdrawSQL = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String depositSQL = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = this.connect(url)) {

            conn.setAutoCommit(false);

            try (PreparedStatement withdraw = conn.prepareStatement(withdrawSQL)) {

                Savepoint savepoint = conn.setSavepoint();

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

    public boolean cardExist(String toCardNumber, String url) {
        String sql = "SELECT * FROM card WHERE number = ?";
        String number = "";

        try (Connection conn = this.connect(url);
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

    public static boolean checkLuhn(String cardNo)
    {
        int nDigits = cardNo.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = cardNo.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }
}


class AccountGenerator {

    public static String generateCardNumber() {
        Random random = new Random();
        int[] identifier = new int[16];
        identifier[0] = 4;

        for (int i = 6; i < 15; i++) {
            identifier[i] = random.nextInt(10);
        }

        int[] tempArr = identifier.clone();
        int control = 0;

        for (int i = 1; i < 16; i++) {
            if (i % 2 != 0) {
                tempArr[i - 1] *= 2;
            }

            if (tempArr[i - 1] > 9) {
                tempArr[i - 1] -= 9;
            }

            control += tempArr[i -1];
        }

        int checksum =  10 - control % 10;

        if (checksum < 10) {
            identifier[15] = checksum;
        }

        StringBuilder builder = new StringBuilder();
        for (int i : identifier) {
            builder.append(i);
        }

        return "" + builder;

    }

    public static String generatePin() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
