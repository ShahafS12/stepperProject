package mta.course.java.stepper.dd.impl.file;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.io.File;
import java.util.Scanner;

public class FileDataDefenition extends AbstractDataDefinition
{
    public FileDataDefenition()  {
        super("List", false, File.class);
    }
    @Override
    public File getValue(String name){
        System.out.println("please enter " + name);
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File file = new File(path);
        return file;
    }
}
