package mta.course.java.stepper.dd.impl.doubledd;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class DoubleDataDefinition extends AbstractDataDefinition {
    public DoubleDataDefinition() {
        super("Double", true, Double.class);
    }
    public Double getValue(String name) {
        System.out.println("Please enter " + name);
        Scanner scanner = new Scanner(System.in);
        Double number = scanner.nextDouble();
        return number;
    }
}
