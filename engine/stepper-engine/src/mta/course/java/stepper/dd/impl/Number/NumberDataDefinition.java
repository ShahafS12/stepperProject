package mta.course.java.stepper.dd.impl.Number;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class NumberDataDefinition extends AbstractDataDefinition {
    public NumberDataDefinition () {super ("Number", true, Number.class); }
    public Number getValue(String name) {
        Scanner scanner = new Scanner(System.in);
        Number number = null;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Please enter " + name);
                String input = scanner.nextLine();
                if (input.contains(".")) {
                    number = Double.parseDouble(input);
                } else {
                    number = Integer.parseInt(input);
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return number;
    }
}
