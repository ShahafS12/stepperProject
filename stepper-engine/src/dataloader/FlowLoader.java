package dataloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import dataloader.generated.STStepper;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.stepper.StepperDefinition;
import mta.course.java.stepper.stepper.StepperDefinitionImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static mta.course.java.stepper.step.StepDefinitionRegistry.fromString;

public class FlowLoader
{
    private FlowDefinition flow;
    public static StepperDefinition loadFlowFromXmlFile(String filePath) throws IOException, InvalidPathException, ParserConfigurationException, SAXException {
        List<FlowDefinition> flows = new ArrayList<>();

        // Check if the file exists and is a valid XML file
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path) || !filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("Invalid XML file path");
        }

        try {
            String packageName = "dataloader.generated";
            JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            STStepper stepperDefinition = (STStepper) jaxbUnmarshaller.unmarshal(new File(filePath));
            StepperDefinition stepper = new StepperDefinitionImpl(stepperDefinition);
            return stepper;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        catch (RuntimeException e){
            throw new RuntimeException("Error initializing stepper definition", e);
        }
    }

}
