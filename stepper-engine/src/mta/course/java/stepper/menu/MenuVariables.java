package mta.course.java.stepper.menu;

import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepExecutionStatistics;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import mta.course.java.stepper.stepper.StepperDefinition;

import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuVariables
{
    private StepperDefinition stepper;
    private ArrayList<String> flowNames;
    private Integer uniqueFlowIdCounter;
    private Map<Integer, FlowExecution> flowExecutionMap;
    private Map<String,FlowExecution> flowExecutionMapFromFlowName;
    private Map<Integer, FlowExecutionStatistics> stats;
    private Integer uniqueFlowExecutionIdCounter;
    private Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsMap;
    private Map<String, SingleStepExecutionData> singleStepExecutionDataMap;
    private Map<String,StepExecutionStatistics> stepExecutionStatisticsMap;
    private ExecutorService executorService;

    public MenuVariables() {
        this.flowNames = new ArrayList<String>();
        this.uniqueFlowIdCounter = 0;
        this.flowExecutionMap = new HashMap<Integer, FlowExecution>();
        this.stats = new HashMap<Integer, FlowExecutionStatistics>();
        this.uniqueFlowExecutionIdCounter = 1;
        this.flowExecutionsStatisticsMap = new HashMap<String, FlowExecutionsStatistics>();
        this.singleStepExecutionDataMap = new HashMap<String, SingleStepExecutionData>();
        this.flowExecutionMapFromFlowName = new HashMap<String, FlowExecution>();
        this.stepExecutionStatisticsMap = new HashMap<String, StepExecutionStatistics>();
        this.executorService = null;
    }
    public Map<String, FlowExecution> getFlowExecutionMapFromFlowName() {
        return flowExecutionMapFromFlowName;
    }
    public void putFlowExecutionMapFromFlowName(String key, FlowExecution value) {
        this.flowExecutionMapFromFlowName.put(key, value);
    }
    public void setExecutorService() {
        this.executorService = Executors.newFixedThreadPool(getMaxThreads());
    }
    public Map<String, StepExecutionStatistics> getStepExecutionStatisticsMap() {
        return stepExecutionStatisticsMap;
    }
    public void putStepExecutionStatisticsMap(String key, StepExecutionStatistics value) {
        if(!this.stepExecutionStatisticsMap.containsKey(key))
            this.stepExecutionStatisticsMap.put(key, value);
        else{
            this.stepExecutionStatisticsMap.get(key).addStepExecutionStatistics(value);
        }
    }

    public StepperDefinition getStepper() {
        return stepper;
    }
    public void upuniqueFlowIdCounter() {
        this.uniqueFlowIdCounter++;
    }

    public void setStepper(StepperDefinition stepper) {
        this.stepper = stepper;
    }
    public void putFlowExecutionMap(Integer key, FlowExecution value) {
        this.flowExecutionMap.put(key, value);
    }
    public void upuniqueFlowExecutionIdCounter() {
        this.uniqueFlowExecutionIdCounter++;
    }
    public ArrayList<String> getFlowNames() {
        return flowNames;
    }
    public void setFlowNames(ArrayList<String> flowNames) {
        this.flowNames = flowNames;
    }
    public Integer getUniqueFlowIdCounter() {
        return uniqueFlowIdCounter;
    }
    public void setUniqueFlowIdCounter(Integer uniqueFlowIdCounter) {
        this.uniqueFlowIdCounter = uniqueFlowIdCounter;
    }
    public Map<Integer, FlowExecution> getFlowExecutionMap() {
        return flowExecutionMap;
    }
    public void setFlowExecutionMap(Map<Integer, FlowExecution> flowExecutionMap) {
        this.flowExecutionMap = flowExecutionMap;
    }
    public Map<Integer, FlowExecutionStatistics> getStats() {
        return stats;
    }
    public void setStats(Map<Integer, FlowExecutionStatistics> stats) {
        this.stats = stats;
    }
    public Integer getUniqueFlowExecutionIdCounter() {
        return uniqueFlowExecutionIdCounter;
    }
    public void setUniqueFlowExecutionIdCounter(Integer uniqueFlowExecutionIdCounter) {
        this.uniqueFlowExecutionIdCounter = uniqueFlowExecutionIdCounter;
    }
    public Map<String, FlowExecutionsStatistics> getFlowExecutionsStatisticsMap() {
        return flowExecutionsStatisticsMap;
    }
    public void setFlowExecutionsStatisticsMap(Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsMap) {
        this.flowExecutionsStatisticsMap = flowExecutionsStatisticsMap;
    }
    public Map<String, SingleStepExecutionData> getSingleStepExecutionDataMap() {
        return singleStepExecutionDataMap;
    }
    public void setSingleStepExecutionDataMap(Map<String, SingleStepExecutionData> singleStepExecutionDataMap) {
        this.singleStepExecutionDataMap = singleStepExecutionDataMap;
    }
    public int getMaxThreads() {
        return stepper.getMaxThreads();
    }

    public void executeFlow(FlowDefinition chosenFlow, List<Control> mandatoryInputs, List<Control> optionalInputs, List<InputWithStepName> outputs, List<SingleStepExecutionData> executionData, CountDownLatch latch)
    {
        executorService.execute(new MyRunnable(chosenFlow, mandatoryInputs, optionalInputs, outputs, executionData, latch));
    }

    class MyRunnable implements Runnable
    {
        private FlowDefinition chosenFlow;
        private List<Control> mandatoryInputs;
        private List<Control> optionalInputs;
        private List<InputWithStepName> outputs;
        private List<SingleStepExecutionData> executionData;
        private CountDownLatch latch;

        public MyRunnable(FlowDefinition chosenFlow, List<Control> mandatoryInputs, List<Control> optionalInputs, List<InputWithStepName> outputs, List<SingleStepExecutionData> executionData,
                          CountDownLatch latch) {
            this.chosenFlow = chosenFlow;
            this.mandatoryInputs = mandatoryInputs;
            this.optionalInputs = optionalInputs;
            this.outputs = outputs;
            this.executionData = executionData;
            this.latch = latch;
        }

        @Override
        public synchronized void run()
        {
            FLowExecutor fLowExecutor = new FLowExecutor();
            stats.put(getUniqueFlowExecutionIdCounter(),
                    fLowExecutor.executeFlowUI(flowExecutionMapFromFlowName.get(chosenFlow.getName()),
                            mandatoryInputs, optionalInputs, outputs, executionData,
                            stepExecutionStatisticsMap));

            upuniqueFlowExecutionIdCounter();
            if (flowExecutionsStatisticsMap.containsKey(chosenFlow.getName())) {
                flowExecutionsStatisticsMap.get(
                        chosenFlow.getName()).addFlowExecutionStatistics(stats.get(getUniqueFlowExecutionIdCounter() - 1));
            } else {
                FlowExecutionsStatistics flowExecutionsStatistics = new FlowExecutionsStatistics(chosenFlow.getName());
                flowExecutionsStatistics.addFlowExecutionStatistics(stats.get(getUniqueFlowExecutionIdCounter() - 1));
                flowExecutionsStatisticsMap.put(chosenFlow.getName(), flowExecutionsStatistics);

            }
            latch.countDown(); // finished the task
        }
    }

}
