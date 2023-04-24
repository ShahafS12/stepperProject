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

import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static mta.course.java.stepper.step.StepDefinitionRegistry.fromString;

public class FlowLoader
{
    private FlowDefinition flow;
    public static List<FlowDefinition> loadFlowFromXmlFile(String filePath) throws IOException, InvalidPathException, ParserConfigurationException, SAXException {
        List<FlowDefinition> flows = new ArrayList<>();

        // Check if the file exists and is a valid XML file
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path) || !filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("Invalid XML file path");
        }

        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));
        
        Set<String> flowNames = new HashSet<>();
        NodeList flowList = doc.getElementsByTagName("ST-Flow");
        checkForDupFlowNames(flowNames, flowList);
        
        NodeList stepList = doc.getElementsByTagName("step");
        checkInvalidStepRef(doc, stepList);

        // Validate the flow
        if (!validateFlow(doc)) {
            throw new IllegalArgumentException("Invalid flow");
        }

        loadFlows(flows, flowList);

        System.out.println("Flows loaded successfully");
        return flows;
    }

    private static void loadFlows(List<FlowDefinition> flows, NodeList flowList)
    {
        for (int i = 0; i < flowList.getLength(); i++) {
            Element flowElement = (Element) flowList.item(i);
            String flowName = flowElement.getAttribute("name");
            String flowDescription = flowElement.getElementsByTagName("ST-FlowDescription").item(0).getTextContent();
            FlowDefinition flow = new FlowDefinitionImpl(flowName, flowDescription);
            String flowOutput = flowElement.getElementsByTagName("ST-FlowOutput").item(0).getTextContent();
            String[] outputNames = flowOutput.split(",");
            for (String outputName : outputNames) {
                flow.addFlowOutput(outputName);
            }
            NodeList fstepList = flowElement.getElementsByTagName("ST-StepInFlow");
            for (i = 0; i < fstepList.getLength(); i++) {
                Element step = (Element) fstepList.item(i);
                String stepName = step.getAttribute("name");
                StepDefinitionRegistry toAdd = fromString(stepName);
                flow.getFlowSteps().add(new StepUsageDeclarationImpl(toAdd.getStepDefinition()));
            }

            // TODO: check if steps loaded successfully
            flows.add(flow);
        }
    }

    private static void checkInvalidStepRef(Document doc, NodeList stepList)
    {
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
    }

    private static void checkForDupFlowNames(Set<String> flowNames, NodeList flowList)
    {
        for (int i = 0; i < flowList.getLength(); i++) {
            Element flow = (Element) flowList.item(i);
            String flowName = flow.getAttribute("name");
            if (flowNames.contains(flowName)) {
                throw new IllegalArgumentException("Duplicate flow name: " + flowName);
            }
            flowNames.add(flowName);
        }
    }

    private static boolean validateFlow(Document doc) {
        // TODO: Implement flow validation
        return true;
    }
}
