package mta.course.java.stepper.flow.manager;

import dataloader.LoadXMLFile;
import javafx.scene.control.Alert;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;

public class FlowManager
{
    private static Set<FlowDefinition> flowsSet;
    //private Integer uniqueFlowExecutionIdCounter;//todo add all menuVariables and flowDefinition functions and variables
    //private Integer uniqueFlowIdCounter;

    public FlowManager() {flowsSet = new HashSet<>();}
    public void addFlow(FlowDefinition flowDefinition)
    {
        flowsSet.add(flowDefinition);
    }
    public static Set<FlowDefinition> getFlows()
    {
        return flowsSet;
    }
    public void addFlows(StepperDefinition stepper){
        for (FlowDefinition flowDefinition : stepper.getFlows())
        {
            flowsSet.add(flowDefinition);//todo: check if it adds the same flow twice
        }
    }
    public void addFlow(File file){
        try {
            MenuVariables newMenuVariables = new MenuVariables();
            LoadXMLFile loadXMLFile = new LoadXMLFile();
            StepperDefinition newStepper = (loadXMLFile.loadXMLFile(file));
            newMenuVariables.setStepper(newStepper);
            newMenuVariables.setExecutorService();
            ArrayList<String> newFlowNames = new ArrayList<>();
            newFlowNames.addAll(newStepper.getFlowNames());//add the latest names of the flows
            newMenuVariables.setFlowNames(newFlowNames);
            //flowIdMap.put(flowNames.get(flowNames.size()-1),uniqueFlowIdCounter);
            for (int i = 0; i < newMenuVariables.getFlowNames().size(); i++) {
                FlowExecution flowExecution = new FlowExecution(newMenuVariables.getFlowNames().get(i), newStepper.getFlowDefinition(newFlowNames.get(i)));
                newMenuVariables.putFlowExecutionMap(newMenuVariables.getUniqueFlowIdCounter(), flowExecution);
                newMenuVariables.putFlowExecutionMapFromFlowName(newMenuVariables.getFlowNames().get(i), flowExecution);
                newMenuVariables.upuniqueFlowIdCounter();
            }
            for(String flowName: newMenuVariables.getFlowNames()){//check if the flow has a continuation to a flow that doesn't exist
                FlowDefinition flowDefinition = newStepper.getFlowDefinition(flowName);
                for(Continuation continuation: flowDefinition.getContinuations()){
                    if(!newMenuVariables.getFlowNames().contains(continuation.getTargetFlow())){
                        throw new Exception("Invalid XML file");
                    }
                    for(String sourceData: continuation.getContinuationMapping().keySet()){
                        Set<String> allFlowInputs = flowDefinition.getAllInputs().keySet();
                        Set<String> allFlowOutputs = flowDefinition.getAllOutputs().keySet();
                        Set<String> allFlowInputsAndOutputsSplitted = new HashSet<>();
                        for(String input: allFlowInputs){
                            allFlowInputsAndOutputsSplitted.add(input.split("\\.")[1]);
                        }
                        for(String output: allFlowOutputs){
                            allFlowInputsAndOutputsSplitted.add(output.split("\\.")[1]);
                        }
                        if(!allFlowInputsAndOutputsSplitted.contains(sourceData)){
                            throw new Exception("Invalid XML file");
                        }
                        for(String targetData: continuation.getContinuationMapping().get(sourceData)){
                            Set<String> allTargetFlowInputs = newStepper.getFlowDefinition(continuation.getTargetFlow()).getAllInputs().keySet();
                            Set<String> allTargetFlowOutputs = newStepper.getFlowDefinition(continuation.getTargetFlow()).getAllOutputs().keySet();
                            Set<String> allTargetFlowInputsAndOutputsSplitted = new HashSet<>();
                            for(String input: allTargetFlowInputs){
                                allTargetFlowInputsAndOutputsSplitted.add(input.split("\\.")[1]);
                            }
                            for(String output: allTargetFlowOutputs){
                                allTargetFlowInputsAndOutputsSplitted.add(output.split("\\.")[1]);
                            }
                            if(!allTargetFlowInputsAndOutputsSplitted.contains(targetData)){
                                throw new Exception("Invalid XML file");
                            }
                        }
                    }
                }
            }
            addFlows(newStepper);
            //currentXMLLabel.setText(file.getAbsolutePath());
            //mainController.setMenuVariables(menuVariables);
            //mainController.setShowFlowComponentController();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading XML file");
            alert.setContentText("Error loading XML file");
            alert.showAndWait();
        }
    }


    public static FlowDefinition getFlowDefinition(String flowName)
    {
        for (FlowDefinition flowDefinition : flowsSet)
        {
            if (flowDefinition.getName().equals(flowName))
            {
                return flowDefinition;
            }
        }
        throw new RuntimeException("Flow" + flowName +"not found");
    }
    public static Set<String> getFlowNames()
    {
        Set<String> flowNames = new HashSet<>();
        for (FlowDefinition flowDefinition : flowsSet)
        {
            flowNames.add(flowDefinition.getName());
        }
        return flowNames;
    }
}
