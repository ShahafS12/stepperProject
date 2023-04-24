package mta.course.java.stepper.dd.impl.number;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class DoubleDataDefinition extends AbstractDataDefinition {
    public DoubleDataDefinition() {
        super("Double", true, Double.class);
    }
    public Double getValue() {
        System.out.println("Please enter a double number:");
        Scanner scanner = new Scanner(System.in);
        Double number = scanner.nextDouble();
        return number;
    }
}
