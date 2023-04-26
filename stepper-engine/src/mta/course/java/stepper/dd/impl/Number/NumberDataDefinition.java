package mta.course.java.stepper.dd.impl.Number;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class NumberDataDefinition extends AbstractDataDefinition {
    public NumberDataDefinition () {super ("Number", true, Number.class); }
    public Number getValue(String name) {
        System.out.println("Please enter " + name);
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        return number;
    }
}
