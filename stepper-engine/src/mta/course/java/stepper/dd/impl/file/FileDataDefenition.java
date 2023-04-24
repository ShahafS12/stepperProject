package mta.course.java.stepper.dd.impl.file;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FileDataDefenition extends AbstractDataDefinition
{
    public FileDataDefenition()  {
        super("List", false, File.class);
    }
    @Override
    public File getValue(){
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File file = new File(path);
        return file;
    }
}
