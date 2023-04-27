package mta.course.java.stepper.dd.impl.doubledd;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class DoubleDataDefinition extends AbstractDataDefinition {
    public DoubleDataDefinition() {
        super("Double", true, Double.class);
    }
    public Double getValue(String name) {
        Scanner scanner = new Scanner(System.in);
        Double number = null;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Please enter " + name);
                String input = scanner.nextLine();
                number = Double.parseDouble(input);
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return number;
    }

}
