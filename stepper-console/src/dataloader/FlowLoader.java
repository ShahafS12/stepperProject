package dataloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.*;
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
    public static List<FlowDefinitionImpl> loadFlowFromXmlFile(String filePath) throws IOException, InvalidPathException, ParserConfigurationException, SAXException {
        List<FlowDefinitionImpl> flows = new ArrayList<>();

        // Check if the file exists and is a valid XML file
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path) || !filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("Invalid XML file path");
        }

        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));
        //doc.getDocumentElement().normalize();

        // Check for duplicate flow names
        Set<String> flowNames = new HashSet<>();
        NodeList flowList = doc.getElementsByTagName("ST-Flow");
        for (int i = 0; i < flowList.getLength(); i++) {
            Element flow = (Element) flowList.item(i);
            String flowName = flow.getAttribute("name");
            if (flowNames.contains(flowName)) {
                throw new IllegalArgumentException("Duplicate flow name: " + flowName);
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
                    throw new IllegalArgumentException("Invalid step reference: " + nextStep);
                }
            }
        }

        // Validate the flow
        if (!validateFlow(doc)) {
            throw new IllegalArgumentException("Invalid flow");
        }

        // Load the flows
        for (int i = 0; i < flowList.getLength(); i++) {
            Element flowElement = (Element) flowList.item(i);
            String flowName = flowElement.getAttribute("name");
            String flowDescription = flowElement.getElementsByTagName("ST-FlowDescription").item(0).getTextContent();
            FlowDefinitionImpl flow = new FlowDefinitionImpl(flowName, flowDescription);
            String flowOutput = flowElement.getElementsByTagName("ST-FlowOutput").item(0).getTextContent();
            String[] outputNames = flowOutput.split(",");
            for (String outputName : outputNames) {
                flow.addFlowOutput(outputName);
            }

            // TODO: Load the steps of the flow
            flows.add(flow);
        }

        System.out.println("Flows loaded successfully");
        return flows;
    }

    private static boolean validateFlow(Document doc) {
        // TODO: Implement flow validation
        return true;
    }
}
