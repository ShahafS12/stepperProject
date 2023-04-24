package mta.course.java.stepper.dd.impl.string;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.Scanner;

public class StringDataDefinition extends AbstractDataDefinition {

    public StringDataDefinition() {
        super("String", true, String.class);
    }
    public String getValue() {
        System.out.println("Please enter a string:");
        Scanner scanner = new Scanner(System.in);
        String string = scanner.nextLine();
        return string;
    }
}
