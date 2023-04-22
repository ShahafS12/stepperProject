package dataloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FlowLoader
{
    private FlowDefinitionImpl flow;
    public static void loadFlowFromXmlFile(String filePath) {
        try {
            // Check if the file exists and is a valid XML file
            Path path = Paths.get(filePath);
            if (!Files.exists(path) || !Files.isRegularFile(path) || !filePath.endsWith(".xml")) {
                System.out.println("Invalid XML file path");
                return;
            }

            // Parse the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            // Check for duplicate flow names
            Set<String> flowNames = new HashSet<>();
            NodeList flowList = doc.getElementsByTagName("flow");
            for (int i = 0; i < flowList.getLength(); i++) {
                Element flow = (Element) flowList.item(i);
                String flowName = flow.getAttribute("name");
                if (flowNames.contains(flowName)) {
                    System.out.println("Duplicate flow name: " + flowName);
                    return;
                }
                flowNames.add(flowName);
            }

            // Check for invalid step references
            NodeList stepList = doc.getElementsByTagName("step");
            for (int i = 0; i < stepList.getLength(); i++) {
                Element step = (Element) stepList.item(i);
                String nextStep = step.getAttribute("nextStep");
                if (!nextStep.isEmpty()) {
                    Element next = doc.getElementById(nextStep);
                    if (next == null) {
                        System.out.println("Invalid step reference: " + nextStep);
                        return;
                    }
                }
            }

            // Validate the flow
            if (!validateFlow(doc)) {
                System.out.println("Invalid flow");
                return;
            }

            // If everything is okay, load the flow
            // TODO: Implement loading the flow
            System.out.println("Flow loaded successfully");

        } catch (IOException e) {
            System.out.println("Error reading XML file: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.out.println("Invalid file path: " + filePath);
        } catch (ParserConfigurationException | SAXException e) {
            System.out.println("Error parsing XML file: " + e.getMessage());
        }
    }

    private static boolean validateFlow(Document doc) {
        // TODO: Implement flow validation
        return true;
    }
}
