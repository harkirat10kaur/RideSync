package util;

public class InputValidator {

    public static double validateDistance(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            System.out.println("Invalid input!");
            return 0;
        }
    }
}