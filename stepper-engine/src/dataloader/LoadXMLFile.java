package dataloader;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static dataloader.FlowLoader.loadFlowFromXmlFile;

public class LoadXMLFile
{
    public StepperDefinition loadXMLFile() throws IOException, ParserConfigurationException, SAXException
    {
        try {
            // Prompt the user for the XML file path
            System.out.println("Enter the full path to the XML file:");
            Scanner scanner = new Scanner(System.in);
            String filePath = scanner.nextLine();

            // Load the flow from the XML file
            return loadFlowFromXmlFile(filePath);
        }
        catch (RuntimeException e){
            throw new RuntimeException("Error initializing stepper definition", e);
        }
    }
    public StepperDefinition loadXMLFile(File file) throws IOException, ParserConfigurationException, SAXException
    {
        try {
            // Load the flow from the XML file
            return loadFlowFromXmlFile(file.getAbsolutePath());
        }
        catch (RuntimeException e){
            throw new RuntimeException("Error initializing stepper definition", e);
        }
    }
}
