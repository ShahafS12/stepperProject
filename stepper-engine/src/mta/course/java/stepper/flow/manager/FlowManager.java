package mta.course.java.stepper.flow.manager;

import dataloader.LoadXMLFile;
import javafx.scene.control.Alert;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepExecutionStatistics;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;

public class FlowManager
{
    private static Set<FlowDefinition> flowsSet;
    private static  Map<String,FlowExecution> flowExecutionMapFromFlowName;
    private static Map<String, StepExecutionStatistics> stepExecutionStatisticsMap;
    private static ExecutorService executorService;
    private static int uniqueFlowIdCounter = 1;
    private static FlowExecutionStatistics currentStatsFlowExecuted;
    private static Map<Integer, FlowExecutionStatistics> stats;
    private static Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsMap;
    private static List<SingleStepExecutionData> latestExecutionData;

    public FlowManager() {
        flowsSet = new HashSet<>();
        flowExecutionMapFromFlowName = new HashMap<>();
        executorService = null;
        this.stepExecutionStatisticsMap = new HashMap<String, StepExecutionStatistics>();
        this.currentStatsFlowExecuted = null;
        this.stats = Collections.synchronizedMap(new HashMap<Integer, FlowExecutionStatistics>());
        this.flowExecutionsStatisticsMap = new HashMap<String, FlowExecutionsStatistics>();
        this.latestExecutionData = new ArrayList<>();
    }
    public void setExecutorService(int maxThreads) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }
    public Map<String, StepExecutionStatistics> getStepExecutionStatisticsMap() {
        return stepExecutionStatisticsMap;
    }
    public void addFlow(FlowDefinition flowDefinition)
    {
        flowsSet.add(flowDefinition);
    }
    public Map<String, FlowExecutionsStatistics> getFlowExecutionsStatisticsMap() {
        return flowExecutionsStatisticsMap;
    }
    public Map<Integer, FlowExecutionStatistics> getStats() { return stats;}
    public static Set<FlowDefinition> getFlows()
    {
        return flowsSet;
    }
    public void addFlows(StepperDefinition stepper){
        if(flowsSet.size()==0){
            setExecutorService(stepper.getMaxThreads());
        }
        for (FlowDefinition flowDefinition : stepper.getFlows())
        {
            flowsSet.add(flowDefinition);//todo: check if it adds the same flow twice
        }
    }
    public int getUniqueFlowIdCounter() {
        return uniqueFlowIdCounter;
    }
    public void upuniqueFlowIdCounter() {
        uniqueFlowIdCounter++;
    }
    public void addFlow(File file){
        try {
            MenuVariables newMenuVariables = new MenuVariables();
            LoadXMLFile loadXMLFile = new LoadXMLFile();
            Map<String,FlowExecution> newflowExecutionMapFromFlowName = new HashMap<>();
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
                //newMenuVariables.putFlowExecutionMapFromFlowName(newMenuVariables.getFlowNames().get(i), flowExecution);
                newflowExecutionMapFromFlowName.put(newMenuVariables.getFlowNames().get(i), flowExecution);
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
            flowExecutionMapFromFlowName.putAll(newflowExecutionMapFromFlowName);
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
    public void addToFlowExecutionMapFromFlowName(Map<String, FlowExecution> flowExecutionMapFromFlowName) {
        this.flowExecutionMapFromFlowName.putAll(flowExecutionMapFromFlowName);
    }
    public static Map<String,FlowExecution> getFlowExecutionMapFromFlowName() {
        return flowExecutionMapFromFlowName;
    }
    public static FlowExecution getFlowExecutionFromFlowName(String flowName) {
        return flowExecutionMapFromFlowName.get(flowName);
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
    public List<SingleStepExecutionData> getLatestExecutionData() {
        return latestExecutionData;
    }
    public void executeFlow(FlowDefinition chosenFlow, List<Object> mandatoryInputs, List<Object> optionalInputs, List<InputWithStepName> outputs,
                            List<SingleStepExecutionData> executionData, CountDownLatch latch, String userName)
    {
        executorService.execute(new MyRunnable(chosenFlow, mandatoryInputs, optionalInputs, outputs, executionData, latch, userName));
    }
    class MyRunnable implements Runnable
    {
        private FlowDefinition chosenFlow;
        private List<Object> mandatoryInputs;
        private List<Object> optionalInputs;
        private List<InputWithStepName> outputs;
        private List<SingleStepExecutionData> executionData;
        private CountDownLatch latch;
        private String userName;
        public MyRunnable(FlowDefinition chosenFlow, List<Object> mandatoryInputs, List<Object> optionalInputs, List<InputWithStepName> outputs,
                          List<SingleStepExecutionData> executionData,CountDownLatch latch, String userName) {
            this.chosenFlow = chosenFlow;
            this.mandatoryInputs = mandatoryInputs;
            this.optionalInputs = optionalInputs;
            this.outputs = outputs;
            this.executionData = executionData;
            latestExecutionData = executionData;
            this.latch = latch;//TODO: check if this is needed
            this.userName = userName;
        }
        public synchronized Integer getUniqueFlowExecutionIdCounter() {
            return uniqueFlowIdCounter;
        }

        @Override
        public void run() {
            int currentFlowExecutionIdCounter;

            synchronized (this) {
                currentFlowExecutionIdCounter = getUniqueFlowExecutionIdCounter();
                upuniqueFlowIdCounter();
            }
            String user;
            FLowExecutor fLowExecutor = new FLowExecutor();
            FlowExecutionStatistics currentStats = fLowExecutor.executeFlowUI(flowExecutionMapFromFlowName.get(chosenFlow.getName()),
                    mandatoryInputs, optionalInputs, outputs, executionData,
                    stepExecutionStatisticsMap, userName);

            synchronized (this) {
                currentStatsFlowExecuted = currentStats;
                stats.put(currentFlowExecutionIdCounter, currentStats);
            }

            synchronized (this) {
                if (flowExecutionsStatisticsMap.containsKey(chosenFlow.getName())) {
                    flowExecutionsStatisticsMap.get(chosenFlow.getName()).addFlowExecutionStatistics(currentStats);
                } else {
                    FlowExecutionsStatistics flowExecutionsStatistics = new FlowExecutionsStatistics(chosenFlow.getName());
                    flowExecutionsStatistics.addFlowExecutionStatistics(currentStats);
                    flowExecutionsStatisticsMap.put(chosenFlow.getName(), flowExecutionsStatistics);
                }
            }

            latch.countDown(); // finished the task//TODO: check if this is needed
        }

    }

}
