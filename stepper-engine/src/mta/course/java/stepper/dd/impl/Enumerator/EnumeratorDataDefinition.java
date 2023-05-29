package mta.course.java.stepper.dd.impl.Enumerator;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;
import mta.course.java.stepper.step.impl.ZipperStep.ZipperEnumerator;

import java.util.Enumeration;
import java.util.Scanner;

public class EnumeratorDataDefinition extends AbstractDataDefinition
{
    public EnumeratorDataDefinition()  {super("Enumerator", true, Enum.class);}
    public String getValue(String name){
        boolean validInput = false;
        String string = "";
        while(!validInput) {
            System.out.println("Please enter a " + name + " from the choices: ");
            ZipperEnumerator[] zipEnum = ZipperEnumerator.values();
            for (ZipperEnumerator i : zipEnum) {
                System.out.println(i);
            }
            Scanner scanner = new Scanner(System.in);
            string = scanner.nextLine();

            for (ZipperEnumerator i : zipEnum){
                if (string.equals(i.toString())) {
                    validInput = true;
                }
            }

            if (!validInput) {
                System.out.println("Invalid Input");
            }
        }
        return string;
    }
}
