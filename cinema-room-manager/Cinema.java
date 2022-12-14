package cinema;

import java.util.Scanner;

public class Cinema {

    public static void main(String[] args) {
        String[][] cinema = createCinema();
        userMenu(cinema);
    }

    public static String[][] createCinema() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of rows:");
        int n = scanner.nextInt();
        System.out.println("Enter the number of seats in each row:");
        int m = scanner.nextInt();

        String[][] cinema = new String[n + 1][m + 1];

        for (int i = 0; i < cinema.length; i++) {
            for (int j = 0; j < cinema[i].length; j++) {
                if (i == 0 && j == 0) {
                    cinema[i][j] = " ";
                } else if (i == 0) {
                    cinema[i][j] = String.valueOf(j);
                } else if (j == 0) {
                    cinema[i][j] = String.valueOf(i);
                } else {
                    cinema[i][j] = "S";
                }
            }
        }
        return cinema;
    }

    public static void userMenu(String[][] cinema) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit");
        int menuOption = scanner.nextInt();

        switch (menuOption) {
            case 1:
                printSeats(cinema);
                userMenu(cinema);
                break;
            case 2:
                buyTicket(cinema);
                userMenu(cinema);
                break;
            case 3:
                printStatistics(cinema);
                userMenu(cinema);
                break;
            case 0:
                break;
        }
    }

    public static void printSeats(String[][] cinema) {
        System.out.println("\nCinema:");
        for (int i = 0; i < cinema.length; i++) {
            for (int j = 0; j < cinema[i].length; j++) {
                System.out.print(cinema[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void buyTicket(String[][] cinema) {
        int rows = cinema.length - 1;
        int seats = cinema[0].length - 1;

        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("\nEnter a row number:");
            int row = scanner.nextInt();
            System.out.println("Enter a seat number in that row:");
            int seat = scanner.nextInt();

            if (row > rows || seat > seats) {
                System.out.println("\nWrong input!");
            }
            else if (cinema[row][seat].equals("B")) {
                System.out.println("\nThat ticket has already been purchased!");
            }
            else {
                int ticketPrice = rows * seats <= 60 ? 10 : row > rows / 2 ? 8 : 10;
                cinema[row][seat] = "B";
                System.out.println("\nTicket price: $" + ticketPrice);
                break;
            }
        }
    }

    public static void printStatistics(String[][] cinema) {
        int rows = cinema.length - 1;
        int seats = cinema[0].length - 1;

        int totalSeats = rows * seats;
        int frontSeats = rows /  2 * seats;
        int backSeats = totalSeats - frontSeats;

        int totalIncome = totalSeats <= 60 ? totalSeats * 10 : frontSeats * 10 + backSeats * 8;

        int ticketsPurchased = 0;
        int currentIncome = 0;

        for (int i = 0; i < cinema.length; i++) {
            for (int j = 0; j < cinema[i].length; j++) {
                if (cinema[i][j].equals("B")) {
                    ticketsPurchased++;
                    if (totalSeats > 60 && i > rows / 2) {
                        currentIncome += 8;
                    } else {
                        currentIncome += 10;
                    }
                }
            }
        }

        double occupancy = 1.0 * ticketsPurchased / (rows * seats) * 100;

        System.out.printf("\nNumber of purchased tickets: %d\n", ticketsPurchased);
        System.out.printf("Percentage: %.2f%%\n", occupancy);
        System.out.printf("Current income: $%d\n", currentIncome);
        System.out.printf("Total income: $%d\n", totalIncome);
    }
}
