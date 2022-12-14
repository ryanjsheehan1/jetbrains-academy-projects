package machine;

import java.util.Scanner;

public class CoffeeMachine {

    private int water;
    private int milk;
    private int beans;
    private int cups;
    private int funds;

    Scanner scanner = new Scanner(System.in);

    public CoffeeMachine() {
        this.water = 400;
        this.milk = 540;
        this.beans = 120;
        this.cups = 9;
        this.funds = 550;
    }

    public void printRemaining() {
        System.out.printf("\nThe coffee machine has:\n" +
                "%d ml of water\n" +
                "%d ml of milk\n" +
                "%d g of coffee beans\n" +
                "%d disposable cups\n" +
                "$%d of money\n", this.water, this.milk, this.beans, this.cups, this.funds);
        System.out.println();
    }

    public void makeEspresso() {
        if (this.water >= 250){
            if (this.beans >= 16){
                if (this.cups > 0){
                    System.out.println("I have enough resources, making you a coffee!\n");
                    this.cups--;
                    this.water -= 250;
                    this.beans -= 16;
                    this.funds += 4;
                }
                else {
                    System.out.println("Sorry, not enough disposable cups!\n");
                }
            }
            else {
                System.out.println("Sorry, not enough beans!\n");
            }
        }
        else {
            System.out.println("Sorry, not enough water!\n");
        }
    }

    public void makeLatte() {
        if (this.water >= 350){
            if (this.milk >= 75){
                if (beans >= 7){
                    if (this.cups > 0 ){
                        System.out.println("I have enough resources, making you a coffee!\n");
                        this.cups--;
                        this.water -= 350;
                        this.milk -= 75;
                        this.beans -= 20;
                        this.funds += 7;
                    }
                    else {
                        System.out.println("Sorry, not enough disposable cups!\n");
                    }
                }
                else {
                    System.out.println("Sorry, not enough bean!\n");
                }
            }
            else {
                System.out.println("Sorry, not enough milk!\n");
            }
        }
        else {
            System.out.println("Sorry, not enough water!\n");
        }
    }

    public void makeCappuccino() {
        if (this.water >= 200){
            if (this.milk >= 100){
                if (this.beans >= 12){
                    if (this.cups > 0){
                        System.out.println("I have enough resources, making you a coffee!\n");
                        this.cups--;
                        this.water -= 200;
                        this.milk -= 100;
                        this.beans -= 12;
                        this.funds += 6;
                    }
                    else {
                        System.out.println("Sorry, not enough disposable cups!\n");
                    }
                }
                else {
                    System.out.println("Sorry, not enough bean!\n");
                }
            }
            else {
                System.out.println("Sorry, not enough milk!\n");
            }
        }
        else {
            System.out.println("Sorry, not enough water!\n");
        }
    }

    public void refill() {
        System.out.println("\nWrite how many ml of water you want to add:");
        int water = scanner.nextInt();
        System.out.println("Write how many ml of milk you want to add:");
        int milk = scanner.nextInt();
        System.out.println("Write how many grams of coffee beans you want to add:");
        int beans = scanner.nextInt();
        System.out.println("Write how many disposable cups of coffee you want to add:");
        int cups = scanner.nextInt();
        System.out.println();
        this.water += water;
        this.milk += milk;
        this.beans += beans;
        this.cups += cups;
    }

    public void withdrawFunds() {
        System.out.println("I gave you $" + this.funds + "\n");
        this.funds = 0;
    }

    public void startUI() {
        while (true) {
            System.out.println("Write action (buy, fill, take, remaining, exit):");
            String action = scanner.next();

            if (action.equals("exit")) {
                break;
            }

            switch (action) {
                case "buy":
                    System.out.println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, " +
                            "back - to main menu:");
                    String option = scanner.next();

                    switch (option) {
                        case "1":
                            makeEspresso();
                            break;
                        case "2":
                            makeLatte();
                            break;
                        case "3":
                            makeCappuccino();
                            break;
                        case "back":
                            System.out.println();
                            continue;
                        default:
                            break;
                    }
                    break;
                case "fill":
                    refill();
                    break;
                case "take":
                    withdrawFunds();
                    break;
                case "remaining":
                    printRemaining();
                    break;
                default:
                    System.out.println("Invalid action\n");
            }
        }
    }
}
