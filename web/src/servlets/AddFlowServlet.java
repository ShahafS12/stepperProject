package servlets;

import com.google.gson.Gson;
import dataloader.LoadXMLFile;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.stepper.StepperDefinition;
import users.UserManager;
import utils.ServletUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddFlowServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)//todo should it be doPost?
    {
        String filePath = request.getParameter("xmlFile");
        File file = new File(filePath);
        FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
        try {//todo take this code to the servlet
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
            for(String flowName: newMenuVariables.getFlowNames()){
                FlowDefinition flowDefinition = newStepper.getFlowDefinition(flowName);
                for(Continuation continuation: flowDefinition.getContinuations()){
                    if(! ( (newMenuVariables.getFlowNames().contains(continuation.getTargetFlow())) || flowManager.getFlowNames().contains(continuation.getTargetFlow()) )){
                        throw new Exception("Invalid XML file");
                    }

                    boolean flag = false; // the target flow is in newMenuVariables but not in flowManager
                    if (!newMenuVariables.getFlowNames().contains(continuation.getTargetFlow())){
                        flag = true; // the target flow is in flowManager
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
                            Set<String> allTargetFlowInputsAndOutputsSplitted = new HashSet<>();

                            if (flag){
                                Set<String> allTargetFlowInputs = flowManager.getFlowDefinition(continuation.getTargetFlow()).getAllInputs().keySet();
                                Set<String> allTargetFlowOutputs = flowManager.getFlowDefinition(continuation.getTargetFlow()).getAllOutputs().keySet();

                                for(String inputsFlowManager : flowManager.getFlowDefinition(continuation.getTargetFlow()).getAllInputs().keySet()){
                                    allTargetFlowInputsAndOutputsSplitted.add(inputsFlowManager.split("\\.")[1]);
                                }
                                for(String outputsFlowManager : flowManager.getFlowDefinition(continuation.getTargetFlow()).getAllOutputs().keySet()){
                                    allTargetFlowInputsAndOutputsSplitted.add(outputsFlowManager.split("\\.")[1]);
                                }
                            }
                            else{
                                Set<String> allTargetFlowInputs = newStepper.getFlowDefinition(continuation.getTargetFlow()).getAllInputs().keySet();
                                Set<String> allTargetFlowOutputs = newStepper.getFlowDefinition(continuation.getTargetFlow()).getAllOutputs().keySet();
                                for(String input: allTargetFlowInputs){
                                    allTargetFlowInputsAndOutputsSplitted.add(input.split("\\.")[1]);
                                }
                                for(String output: allTargetFlowOutputs){
                                    allTargetFlowInputsAndOutputsSplitted.add(output.split("\\.")[1]);
                                }
                            }
                            if(!allTargetFlowInputsAndOutputsSplitted.contains(targetData)){
                                throw new Exception("Invalid XML file");
                            }
                        }
                    }
                }
            }
            flowManager.addFlows(newStepper);
            //print data in flow manager to check if it was added
            flowManager.addToFlowExecutionMapFromFlowName(newMenuVariables.getFlowExecutionMapFromFlowName());
            System.out.println("Flows in flow manager:");
            for(FlowDefinition flowDefinition: FlowManager.getFlows()){
                System.out.println(flowDefinition.getName());
            }
        } catch (Exception e) {
            System.out.println("Invalid XML file");
            e.printStackTrace();
        }
    }
}
