package banking;

import java.util.Random;
import java.util.Scanner;

/**
 *  This class creates a new bank database, handles user interaction of the menu,
 *  and generates and validates bank account card numbers.
 */
public class Bank {

    private final String url;
    Database db = new Database();

    public Bank(String url) {
        this.url = url;
    }

    /**
     * Creates a new SQLite database and table
     */
    public void run() {
        db.createNewDatabase(url);
        db.createNewTable(url);
        mainMenu();
    }

    /**
     * Runs the main menu
     */
    public void mainMenu() {

        int id = 0;

        while(true) {
            System.out.println("\n1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner sc = new Scanner(System.in);

            System.out.print(">");
            int input = sc.nextInt();

            if (input == 1) {
                id++;
                String number = generateCardNumber();
                String pin = generatePin();
                db.insert(id, number, pin, url);
                System.out.println("\nYour card number has been created");
                System.out.println("Your card number:\n" + number);
                System.out.println("Your card PIN:\n" + pin);
            }

            if (input == 2) {
                System.out.print("\nEnter your card number:\n>");
                String number = sc.next();
                System.out.print("Enter you PIN:\n>");
                String pin = sc.next();

                if (db.login(number, pin, url)) {
                    System.out.println("\nYou have successfully logged in!");
                    userMenu(number, pin);
                }
            }

            if (input == 0) {
                System.out.println("\nBye!");
                break;
            }
        }
    }

    /**
     * Runs the user menu
     * @param number Bank card number
     * @param pin Bank card PIN
     */
    public void userMenu(String number, String pin) {

        while(true) {
            System.out.println("\n1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");

            Scanner sc = new Scanner(System.in);
            int input = sc.nextInt();

            switch (input) {
                case 1:
                    System.out.println("\nBalance: " + db.getBalance(number, pin, url));
                    break;

                case 2:
                    System.out.println("\nEnter income:");
                    int income  = sc.nextInt();
                    db.addIncome(number, pin, url, income);
                    break;

                case 3:
                    System.out.println("\nTransfer\nEnter card number:");
                    String toAccountNumber = sc.next();

                    if (!checkLuhn(toAccountNumber)) {
                        System.out.println("Probably you made a mistake in the card number. " +
                                "Please try again!");
                        break;
                    }

                    if(!db.cardExist(toAccountNumber, url)) {
                        System.out.println("Such a card does not exist.");
                        break;
                    }

                    System.out.println("Enter how much money you want to transfer:");
                    int amount = sc.nextInt();

                    if (number.equals(toAccountNumber)) {
                        System.out.println("You can't transfer money to the same account!");
                        break;
                    }

                    if (amount > db.getBalance(number, pin, url)) {
                        System.out.println("Not enough money!");
                        break;
                    }

                    db.transfer(number, toAccountNumber, amount, url);
                    break;

                case 4:
                    db.closeAccount(number, url);
                    System.out.println("\nThe account has been closed!");
                    break;

                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
            }
        }
    }

    /**
     * Generates a pseudorandom bank card number.
     * The generated bank card number is 16 digits in length.
     * The first six digits is the bank identification number which is "4000000".
     * The last 10 digits is the account identifier.
     * The final digit is the checksum.
     * <p>
     * The Luhn algorithm is implemented to validate the generated pseudorandom bank card number.
     *
     * @return Bank card account number
     */
    public String generateCardNumber() {
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

    /**
     * Generates a pseudorandom bank card account PIN.
     * The PIN is four digits in length.
     * @return String bank card account PIN
     */
    public String generatePin() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    /**
     * Checks whether a bank account card number is valid using the Luhn algorithm
     * @param cardNumber
     * @return whether the bank card account number is valid
     */
    public boolean checkLuhn(String cardNumber) {
        int nDigits = cardNumber.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = cardNumber.charAt(i) - '0';

            if (isSecond) {
                d = d * 2;
            }

            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }
}