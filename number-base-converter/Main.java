package converter;

import java.math.BigInteger;
import java.util.Scanner;

public class Main {

    private static final String DIGITS = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter two numbers in format: {source base} {target base}" +
                    " (To quit type /exit) ");
            String[] input = scanner.nextLine().split(" ");

            if(input[0].equals("/exit")) {
                break;
            }

            int sourceBase = Integer.parseInt(input[0]);
            int targetBase = Integer.parseInt(input[1]);

            while (true) {
                System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) ",
                        sourceBase, targetBase);
                String num = scanner.nextLine();

                if(num.equals("/back")){
                    System.out.println();
                    break;
                }

                System.out.println("Conversion result: " + fromToRadix(num, sourceBase, targetBase) +"\n");
            }
        }
    }

    public static String fromToRadix(String num, int sourceBase, int targetBase) {
        int dotIndex = num.indexOf('.');
        if(dotIndex == -1) {
            return new BigInteger(num, sourceBase).toString(targetBase);
        }

        String integral = num.substring(0, dotIndex);
        String fraction = num.substring(1 + dotIndex);
        String targetIntegral = new BigInteger(integral, sourceBase).toString(targetBase);
        double decimalFraction = 0.0;
        double divider = sourceBase;

        for(int digit : fraction.toCharArray()) {
            decimalFraction += DIGITS.indexOf(digit) / divider;
            divider *= sourceBase;
        }

        StringBuilder targetFraction = new StringBuilder();

        for(int i = 5; i > 0; --i) {
            decimalFraction *= targetBase;
            int index = (int) decimalFraction;
            targetFraction.append(DIGITS.charAt(index));
            decimalFraction -= index;
        }

        return targetIntegral + "." + targetFraction;
    }
}
