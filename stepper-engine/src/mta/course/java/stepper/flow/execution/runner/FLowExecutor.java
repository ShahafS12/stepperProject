package mta.course.java.stepper.flow.execution.runner;

import javafx.scene.control.*;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;
import mta.course.java.stepper.step.api.*;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FLowExecutor {

    public FlowExecutionStatistics executeFlow(FlowExecution flowExecution)
    {

        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");
        flowExecution.addingExectionCounter();
        List<InputWithStepName> mandatoryInputs = new ArrayList<>();
        List<InputWithStepName> optionalInputs = new ArrayList<>();
        Time executionStartTime = new Time(System.currentTimeMillis());
        Map<String, Object> dataValues = new HashMap<>();
        Map<String, DataDefinition> dataDefinitions = new HashMap<>();
        FlowExecutionResult flowResult = FlowExecutionResult.SUCCESS;

        StepExecutionContext context = new StepExecutionContextImpl(flowExecution.getFlowDefinition()); // actual object goes here...
        List<StepUsageDeclaration> steps = flowExecution.getFlowDefinition().getFlowSteps();
        boolean finishedEnteringInputs = false;
        List<String> flowFreeInputsString = flowExecution.getFlowDefinition().getPreAliasFlowFreeInputs();
        List<String> flowFreeOutputsString = flowExecution.getFlowDefinition().getFlowFreeOutputsString();
        for (StepUsageDeclaration step1 : steps) {
            StepDefinition stepDefinition = step1.getStepDefinition();
            List<DataDefinitionDeclaration> inputs = stepDefinition.inputs();
            List<DataDefinitionDeclaration> outputs = stepDefinition.outputs();
            for (DataDefinitionDeclaration input : inputs) {
                if (flowFreeInputsString.contains(step1.getFinalStepName() + "." + input.getName())) {
                    if (input.necessity().equals(DataNecessity.MANDATORY)) {
                        mandatoryInputs.add(new InputWithStepName(step1.getFinalStepName(), input));
                    } else {
                        optionalInputs.add(new InputWithStepName(step1.getFinalStepName(), input));
                    }
                }
            }
        }
        for (StepUsageDeclaration step : steps) {
            StepDefinition stepDefinition = step.getStepDefinition();
            List<DataDefinitionDeclaration> inputs = stepDefinition.inputs();
            List<DataDefinitionDeclaration> outputs = stepDefinition.outputs();
            context.addStepAlias(step.getStepName(), step.getFinalStepName(), step.skipIfFail());
            while (!finishedEnteringInputs) {
                printFlowFreeInputs(mandatoryInputs, optionalInputs);
                Scanner scanner = new Scanner(System.in);
                try {
                    int choice = scanner.nextInt();
                    if (choice <= mandatoryInputs.size()) {//saving values in map to add to context later
                        Object value = mandatoryInputs.get(choice - 1).getDataDefinitionDeclaration().dataDefinition().getValue(mandatoryInputs.get(choice - 1).getDataDefinitionDeclaration().userString());
                        DataDefinition dataDefinition = mandatoryInputs.get(choice - 1).getDataDefinitionDeclaration().dataDefinition();
                        dataValues.put(mandatoryInputs.get(choice - 1).getStepName() + "." + mandatoryInputs.get(choice - 1).getDataDefinitionDeclaration().getName(), value);
                        dataDefinitions.put(mandatoryInputs.get(choice - 1).getStepName() + "." + mandatoryInputs.get(choice - 1).getDataDefinitionDeclaration().getName(), dataDefinition);
                        mandatoryInputs.remove(choice - 1);
                    } else if (choice <= mandatoryInputs.size() + optionalInputs.size()) {//saving values in map to add to context later
                        Object value = optionalInputs.get(choice - 1 - mandatoryInputs.size()).getDataDefinitionDeclaration().dataDefinition().
                                getValue(optionalInputs.get(choice - 1 - mandatoryInputs.size()).getDataDefinitionDeclaration().userString());
                        DataDefinition dataDefinition = optionalInputs.get(choice - 1 - mandatoryInputs.size()).getDataDefinitionDeclaration().dataDefinition();
                        dataValues.put(optionalInputs.get(choice - 1 - mandatoryInputs.size()).getStepName() + "." +
                                optionalInputs.get(choice - 1 - mandatoryInputs.size()).getDataDefinitionDeclaration().getName(), value);
                        dataDefinitions.put(optionalInputs.get(choice - 1 - mandatoryInputs.size()).getStepName() + "." +
                                optionalInputs.get(choice - 1 - mandatoryInputs.size()).getDataDefinitionDeclaration().getName(), dataDefinition);
                        optionalInputs.remove(choice - 1 - mandatoryInputs.size());
                    } else {
                        System.out.println("Invalid input");
                    }
                    if(mandatoryInputs.size() == 0 && optionalInputs.size() == 0){
                        finishedEnteringInputs = true;
                    }
                    else if (mandatoryInputs.size() == 0 && optionalInputs.size() > 0) {
                        System.out.println("Do you want to enter more inputs? (Y/N)");
                        String answer = scanner.next();
                        if (answer.equals("N")) {
                            finishedEnteringInputs = true;
                        } else if (answer.equals("Y")) {
                            finishedEnteringInputs = false;
                        } else {
                            while (!answer.equals("Y") && !answer.equals("N")) {
                                System.out.println("Invalid input, please enter Y/N");
                                answer = scanner.next();
                            }
                            if (answer.equals("N")) {
                                finishedEnteringInputs = true;
                            } else if (answer.equals("Y")) {
                                finishedEnteringInputs = false;
                            }
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter a number");
                }
            }
            for (InputWithStepName optionalInput : optionalInputs) {//if the user didn't enter optional inputs, we need to add them to the dataValues
                dataValues.put(optionalInput.getStepName() + "." + optionalInput.getDataDefinitionDeclaration().getName(), "");
                dataDefinitions.put(optionalInput.getStepName() + "." + optionalInput.getDataDefinitionDeclaration().getName(), optionalInput.getDataDefinitionDeclaration().dataDefinition());
            }
            for (DataDefinitionDeclaration input : inputs) {//adding the inputs to the context by the order of the flowFreeInputsString
                if (flowFreeInputsString.contains(step.getFinalStepName() + "." + input.getName())) {
                    context.addStep(step.getFinalStepName() + "." + input.getName(),
                            dataValues.get(step.getFinalStepName() + "." + input.getName()),
                            dataDefinitions.get(step.getFinalStepName() + "." + input.getName()),
                            flowExecution.getFlowDefinition().getFlowLevelAlias(step.getFinalStepName() + "." + input.getName()),
                            flowExecution.getFlowDefinition().getFlowLevelCustomMapping(step.getFinalStepName() + "." + input.getName())
                    );
                }
            }
            for (DataDefinitionDeclaration output : outputs) {
                context.addStep(step.getFinalStepName() + "." + output.getName(),
                        output.dataDefinition().getType(),
                        output.dataDefinition(),
                        flowExecution.getFlowDefinition().getFlowLevelAlias(step.getFinalStepName() + "." + output.getName()),
                        flowExecution.getFlowDefinition().getFlowLevelCustomMapping(step.getFinalStepName() + "." + output.getName())
                );
            }
        }

        // start actual execution
        try {
            for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
                StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
                System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
                Instant start = Instant.now();
                StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
                if (stepResult == StepResult.FAILURE) {
                    flowResult = FlowExecutionResult.FAILURE;
                    if (!flowExecution.getFlowDefinition().getFlowSteps().get(i).skipIfFail()) {
                        throw new RuntimeException("Step " + stepUsageDeclaration.getFinalStepName() + " failed");
                    }
                }
                if (stepResult == StepResult.WARNING && flowResult != FlowExecutionResult.FAILURE)
                    flowResult = FlowExecutionResult.WARNING;
                Instant end = Instant.now();
                double totalTime = Duration.between(start, end).toMillis();
                if (flowExecution.getStepExecutionStatisticsMap().containsKey(stepUsageDeclaration.getFinalStepName())) {
                    flowExecution.getStepExecutionStatisticsMap().get(stepUsageDeclaration.getFinalStepName()).addStepExecutionStatistics(new StepExecutionStatistics(totalTime));
                } else {
                    flowExecution.getStepExecutionStatisticsMap().put(stepUsageDeclaration.getFinalStepName(), new StepExecutionStatistics(totalTime));
                }
                flowExecution.getSingleStepExecutionDataMap().put(stepUsageDeclaration.getFinalStepName(), new SingleStepExecutionData(totalTime, stepResult,
                        context.getSummaryLine(stepUsageDeclaration.getFinalStepName()),
                        context.getLogs(stepUsageDeclaration.getFinalStepName()),
                        stepUsageDeclaration.getFinalStepName()));
                System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
                // check if should continue etc..
            }
            flowExecution.setFlowExecutionResult(flowResult);
            double totalTimeFlow = flowExecution.timeTakenForFlow();
            System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
            List<String> flowFreeOutputs = flowExecution.getFlowDefinition().getFlowFreeOutputsString();
            int i = 0;
            for (DataDefinitionDeclaration output : flowExecution.getFlowDefinition().getFlowFreeOutputs()) {
                String tmp = flowFreeOutputs.get(i);
                System.out.println("\nUser string: " + output.userString() + " Value:\n" + context.getDataValuesMap().get(tmp).toString());
                i++;
            }
            System.out.println("Total Time: " + totalTimeFlow + " ms");
            FlowExecutionStatistics result = new FlowExecutionStatistics(executionStartTime, flowExecution.getFlowDefinition().getName(), flowExecution.getUniqueId()
                    , flowResult, totalTimeFlow, flowExecution.getFlowDefinition(), context, flowExecution.getSingleStepExecutionDataMap(), flowExecution.getStepExecutionStatisticsMap(), "");
            return result;
        }
        catch (RuntimeException e)
        {
            System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
            double totalTimeFlow = flowExecution.timeTakenForFlow();
            System.out.println("Total Time: " + totalTimeFlow + " ms");
            FlowExecutionStatistics result = new FlowExecutionStatistics(executionStartTime, flowExecution.getFlowDefinition().getName(), flowExecution.getUniqueId()
                    , flowResult, totalTimeFlow, flowExecution.getFlowDefinition(), context, flowExecution.getSingleStepExecutionDataMap(), flowExecution.getStepExecutionStatisticsMap(), "");
            return result;
        }
    }
    private void printFlowFreeInputs(List<InputWithStepName> mandatoryInputs, List<InputWithStepName> optionalInputs) {
        System.out.println("Please choose an input to enter:");
        int i = 1;
        System.out.println("Mandatory inputs:\n");
        for (InputWithStepName input : mandatoryInputs) {
            System.out.println(i + ") " + input.getDataDefinitionDeclaration().userString());
            i++;
        }
        System.out.println("\nOptional inputs:\n");
        for (InputWithStepName input : optionalInputs) {
            System.out.println(i + ") " + input.getDataDefinitionDeclaration().userString());
            i++;
        }
    }
    public FlowExecutionStatistics executeFlowUI(FlowExecution flowExecution, List<Object> mandatoryInputsTXT, List<Object> optionalInputsTXT,
                                                 List<InputWithStepName> outputs,List<SingleStepExecutionData> singleStepExecutionDataList,
                                                 Map<String, StepExecutionStatistics> stepExecutionStatisticsMap, String executedUser, Map<String, String> flowLevelAliasingMap,
                                                 Map<String, Class<?>> allInputs){
        List<InputWithStepName> mandatoryInputs = flowExecution.getFlowDefinition().getMandatoryInputs();
        List<InputWithStepName> optionalInputs = flowExecution.getFlowDefinition().getOptionalInputs();
        Instant executionStartTimeInstant = Instant.now();
        Time executionStartTime = new Time(System.currentTimeMillis());
        FlowExecutionResult flowResult = FlowExecutionResult.SUCCESS;
        StepExecutionContext context = new StepExecutionContextImpl(flowExecution.getFlowDefinition());
        Map<String, Object> dataValues = new HashMap<>();
        Map<String, DataDefinition> dataDefinitions = new HashMap<>();
        for (int i=0; i<mandatoryInputs.size(); i++){
            context.addStep(mandatoryInputs.get(i).getStepName() + "." + mandatoryInputs.get(i).getDataDefinitionDeclaration().getName(),
                    mandatoryInputsTXT.get(i),
                    mandatoryInputs.get(i).getDataDefinitionDeclaration().dataDefinition(),
                    flowExecution.getFlowDefinition().getFlowLevelAlias(mandatoryInputs.get(i).getStepName() + "." + mandatoryInputs.get(i).getDataDefinitionDeclaration().getName()),
                    flowExecution.getFlowDefinition().getFlowLevelCustomMapping(mandatoryInputs.get(i).getStepName() + "." + mandatoryInputs.get(i).getDataDefinitionDeclaration().getName())
            );
        }
        for (int i=0; i<optionalInputs.size(); i++){
            context.addStep(optionalInputs.get(i).getStepName() + "." + optionalInputs.get(i).getDataDefinitionDeclaration().getName(),
                    optionalInputsTXT.get(i),
                    optionalInputs.get(i).getDataDefinitionDeclaration().dataDefinition(),
                    flowExecution.getFlowDefinition().getFlowLevelAlias(optionalInputs.get(i).getStepName() + "." + optionalInputs.get(i).getDataDefinitionDeclaration().getName()),
                    flowExecution.getFlowDefinition().getFlowLevelCustomMapping(optionalInputs.get(i).getStepName() + "." + optionalInputs.get(i).getDataDefinitionDeclaration().getName())
            );
        }
        for(InputWithStepName output : outputs){
            context.addStep(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName(),
                    output.getDataDefinitionDeclaration().dataDefinition().getType(),
                    output.getDataDefinitionDeclaration().dataDefinition(),
                    flowExecution.getFlowDefinition().getFlowLevelAlias(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName()),
                    flowExecution.getFlowDefinition().getFlowLevelCustomMapping(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName())
            );
        }
        for(StepUsageDeclaration step : flowExecution.getFlowDefinition().getFlowSteps()){
            context.addStepAlias(step.getStepName(), step.getFinalStepName(), step.skipIfFail());
        }
        for (String key : flowLevelAliasingMap.keySet()){
            String value = flowLevelAliasingMap.get(key);
            context.addFlowLevelAlias(key, value, allInputs.get(value));
        }
        // start actual execution
        try {
            for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
                StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
                System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
                Instant start = Instant.now();
                StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
                if (stepResult == StepResult.FAILURE) {
                    flowResult = FlowExecutionResult.FAILURE;
                    if (!flowExecution.getFlowDefinition().getFlowSteps().get(i).skipIfFail()) {
                        throw new RuntimeException("Step " + stepUsageDeclaration.getFinalStepName() + " failed");
                    }
                }
                if (stepResult == StepResult.WARNING && flowResult != FlowExecutionResult.FAILURE)
                    flowResult = FlowExecutionResult.WARNING;
                Instant end = Instant.now();
                double totalTime = Duration.between(start, end).toMillis();
                if (flowExecution.getStepExecutionStatisticsMap().containsKey(stepUsageDeclaration.getFinalStepName())) {
                    flowExecution.getStepExecutionStatisticsMap().get(stepUsageDeclaration.getFinalStepName()).addStepExecutionStatistics(new StepExecutionStatistics(totalTime));
                } else {
                    flowExecution.getStepExecutionStatisticsMap().put(stepUsageDeclaration.getFinalStepName(), new StepExecutionStatistics(totalTime));
                }
                if(stepExecutionStatisticsMap.containsKey(stepUsageDeclaration.getStepName())){
                    stepExecutionStatisticsMap.get(stepUsageDeclaration.getStepName()).addStepExecutionStatistics(
                            new StepExecutionStatistics(stepUsageDeclaration.getStepName(),totalTime));
                } else {
                    stepExecutionStatisticsMap.put(stepUsageDeclaration.getStepName(), new StepExecutionStatistics(stepUsageDeclaration.getStepName(),totalTime));
                }
                singleStepExecutionDataList.add(new SingleStepExecutionData(totalTime, stepResult,
                        context.getSummaryLine(stepUsageDeclaration.getFinalStepName()),
                        context.getLogs(stepUsageDeclaration.getFinalStepName()),
                        stepUsageDeclaration.getStepName()));
                System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
                // check if should continue etc..
            }
            flowExecution.setFlowExecutionResult(flowResult);
            Instant executionEndTime = Instant.now();
            double totalTimeFlow = Duration.between(executionStartTimeInstant, executionEndTime).toMillis();
            System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
            List<String> flowFreeOutputs = flowExecution.getFlowDefinition().getFlowFreeOutputsString();
            int i = 0;
            for (DataDefinitionDeclaration output : flowExecution.getFlowDefinition().getFlowFreeOutputs()) {
                String tmp = flowFreeOutputs.get(i);
                System.out.println("\nUser string: " + output.userString() + " Value:\n" + context.getDataValuesMap().get(tmp).toString());
                i++;
            }
            System.out.println("Total Time: " + totalTimeFlow + " ms");
            FlowExecutionStatistics result = new FlowExecutionStatistics(executionStartTime, flowExecution.getFlowDefinition().getName(), flowExecution.getUniqueId()
                    , flowResult, totalTimeFlow, flowExecution.getFlowDefinition(), context, flowExecution.getSingleStepExecutionDataMap(), flowExecution.getStepExecutionStatisticsMap(),
                    singleStepExecutionDataList, executedUser);
            return result;
        }
        catch (RuntimeException e)
        {
            flowResult = FlowExecutionResult.FAILURE;
            System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
            double totalTimeFlow = Duration.between(executionStartTimeInstant, Instant.now()).toMillis();
            System.out.println("Total Time: " + totalTimeFlow + " ms");
            FlowExecutionStatistics result = new FlowExecutionStatistics(executionStartTime, flowExecution.getFlowDefinition().getName(), flowExecution.getUniqueId()
                    , flowResult, totalTimeFlow, flowExecution.getFlowDefinition(), context, flowExecution.getSingleStepExecutionDataMap(), flowExecution.getStepExecutionStatisticsMap(),
                    singleStepExecutionDataList, executedUser);
            return result;
        }
    }
    private Object getControlContent(Control c){
        if(c instanceof TextField){
            return ((TextField) c).getText();
        }
        else if (c instanceof Spinner<?>){
            return ((Spinner<?>) c).getValue();
        }
        else if(c instanceof ChoiceBox<?>){
            return ((ChoiceBox<?>) c).getValue().toString();
        }
        return  null;
    }
}
