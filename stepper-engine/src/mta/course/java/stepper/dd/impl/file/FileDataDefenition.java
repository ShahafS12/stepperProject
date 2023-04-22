package mta.course.java.stepper.dd.impl.file;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.io.File;
import java.util.ArrayList;

public class FileDataDefenition extends AbstractDataDefinition
{
    public FileDataDefenition()  {
        super("List", false, File.class);
    }
}
