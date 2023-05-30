package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STFlow;
import dataloader.generated.*;
import dataloader.generated.STStepsInFlow;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataNecessity;

import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<String> flowFreeInputs;
    private final String uniqueId;
    private final List<String> preAliasFlowFreeInputs;
    private final Map<String,Class<?>> allFlowInputs;
    private final Map<String,Class<?>> allFlowOutputs;
    private final Map<String,String> innitialDataValues;
    private final List<DataDefinitionDeclaration> flowFreeInputsDataDefenition;
    private final List<String> flowFreeOutputs;
    private final List<DataDefinitionDeclaration> flowFreeOutputsDataDefenition;
    private final List<StepUsageDeclaration> steps;
    private final List<String> finalStepNames;
    private final Map<String,String> flowLevelAlias;
    private final Map<String, String> customMapping;
    private final Map<String,String> AutoMappingMap;
    private final Map<String, String> ContinuationMap;
    private final List<InputWithStepName> mandatoryInputs;
    private final List<InputWithStepName> optionalInputs;
    private final List<InputWithStepName> outputs;

    private boolean readonly;

    public FlowDefinitionImpl(String name, String description) {//TODO delete this after we are sure we don't need it
        this.name = name;
        this.description = description;
        mandatoryInputs = new ArrayList<>();
        optionalInputs = new ArrayList<>();
        flowFreeInputsDataDefenition = new ArrayList<>();
        flowFreeOutputsDataDefenition = new ArrayList<>();
        flowOutputs = new ArrayList<>();
        flowFreeOutputs = new ArrayList<>();
        innitialDataValues = new HashMap<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
        flowFreeInputs = new ArrayList<>();
        readonly = true;
        allFlowInputs = new HashMap<>();
        allFlowOutputs = new HashMap<>();
        finalStepNames = new ArrayList<>();
        AutoMappingMap = new HashMap<>();
        preAliasFlowFreeInputs = new ArrayList<>();
        ContinuationMap = new HashMap<>();
        uniqueId = UUID.randomUUID().toString();
        outputs = new ArrayList<>();
    }

    public FlowDefinitionImpl(STFlow stFlow)
    {
        readonly = true;
        this.name = stFlow.getName();
        flowFreeInputs = new ArrayList<>();
        mandatoryInputs = new ArrayList<>();
        optionalInputs = new ArrayList<>();
        flowFreeInputsDataDefenition = new ArrayList<>();
        flowFreeOutputsDataDefenition = new ArrayList<>();
        preAliasFlowFreeInputs = new ArrayList<>();
        allFlowInputs = new HashMap<>();
        allFlowOutputs = new HashMap<>();
        finalStepNames = new ArrayList<>();
        AutoMappingMap = new HashMap<>();
        outputs = new ArrayList<>();
        uniqueId = UUID.randomUUID().toString();
        this.description = stFlow.getSTFlowDescription();
        String[] output = stFlow.getSTFlowOutput().split(",");
        flowOutputs = Arrays.asList(output);
        flowFreeOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
        innitialDataValues = new HashMap<>();
        ContinuationMap = new HashMap<>();

        STStepsInFlow stStepsInFlow = stFlow.getSTStepsInFlow();
        for (int i = 0; i < stStepsInFlow.getSTStepInFlow().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = new StepUsageDeclarationImpl(stStepsInFlow.getSTStepInFlow().get(i));
            steps.add(stepUsageDeclaration);
            finalStepNames.add(stepUsageDeclaration.getFinalStepName());
            if (!stepUsageDeclaration.getStepDefinition().isReadonly())//if we found a step that is not readonly, the flow is not readonly
                readonly = false;
        }
        STFlowLevelAliasing stFlowLevelAliasing = stFlow.getSTFlowLevelAliasing();
        if (stFlowLevelAliasing != null) {
            List<STFlowLevelAlias> stFlowLevelAliasList = stFlowLevelAliasing.getSTFlowLevelAlias();
            for (STFlowLevelAlias stFlowLevelAlias : stFlowLevelAliasList) {
                flowLevelAlias.put(stFlowLevelAlias.getStep() + "." + stFlowLevelAlias.getSourceDataName(),
                        stFlowLevelAlias.getStep() + "." + stFlowLevelAlias.getAlias());
            }
        }
        STCustomMappings stCustomMappings = stFlow.getSTCustomMappings();
        if (stCustomMappings != null) {
            List<STCustomMapping> stCustomMappingList = stCustomMappings.getSTCustomMapping();
            for (STCustomMapping stCustomMapping : stCustomMappingList) {
                customMapping.put(stCustomMapping.getSourceStep() + "." + stCustomMapping.getSourceData(),
                        stCustomMapping.getTargetStep() + "." + stCustomMapping.getTargetData());
            }
        }
        if (stFlow.getSTInitialInputValues() != null) {
            for (STInitialInputValue stInitialInputValue : stFlow.getSTInitialInputValues().getSTInitialInputValue()) {
                innitialDataValues.put(stInitialInputValue.getInputName(), stInitialInputValue.getInitialValue());
            }
        }
                STContinuations stContinuationsMap = stFlow.getSTContinuations();
                if (stContinuationsMap != null) {
                    List<STContinuation> stFlowContinuationList = stContinuationsMap.getSTContinuation();
                    for (STContinuation stContinuation : stFlowContinuationList) {
                        List<STContinuationMapping> stContinueMap = stContinuation.getSTContinuationMapping();
                        for (STContinuationMapping i : stContinueMap) {
                            ContinuationMap.put(name + "." + stContinuation.getTargetFlow(),
                                    i.getSourceData() + "." + i.getTargetData());
                        }
                    }
                }
                for (int i = 0; i < steps.size(); i++) {
                    List<DataDefinitionDeclaration> inputStep = steps.get(i).getStepDefinition().inputs();
                    List<DataDefinitionDeclaration> outputStep = steps.get(i).getStepDefinition().outputs();
                    for (DataDefinitionDeclaration dataInput : inputStep) {
                        String inputToAdd = flowLevelAlias.get(steps.get(i).getFinalStepName() + "." + dataInput.getName());
                        if (inputToAdd == null)
                            inputToAdd = steps.get(i).getFinalStepName() + "." + dataInput.getName();
                        allFlowInputs.put(inputToAdd, dataInput.dataDefinition().getType());
                        if (allFlowOutputs.containsKey(inputToAdd) && !AutoMappingMap.containsKey(steps.get(i).getFinalStepName() + "." + inputToAdd))
                            AutoMappingMap.put(steps.get(i).getFinalStepName() + "." + inputToAdd, inputToAdd);

                    }
                    for (DataDefinitionDeclaration dataOutput : outputStep) {
                        String outputToAdd = flowLevelAlias.get(steps.get(i).getFinalStepName() + "." + dataOutput.getName());
                        if (outputToAdd == null)
                            outputToAdd = steps.get(i).getFinalStepName() + "." + dataOutput.getName();
                        allFlowOutputs.put(outputToAdd, dataOutput.dataDefinition().getType());

                    }
                }
                createFreeInputOutputLists();
                FlowValidator flowValidator = new FlowValidator(this);
                boolean flowIsValid = flowValidator.validate();
                if (!flowIsValid)
                    throw new RuntimeException("Flow is not valid");

                int counter = 0;
                for (DataDefinitionDeclaration input : flowFreeInputsDataDefenition) {
                    String key = getStepName(counter++);
                    if (input.necessity().equals(DataNecessity.MANDATORY)) {
                        mandatoryInputs.add(new InputWithStepName(key, input));
                    } else {
                        optionalInputs.add(new InputWithStepName(key, input));
                    }
                }
        initializeOutputs();
    }
    private void initializeOutputs() {
        for (StepUsageDeclaration step : steps) {
            for(DataDefinitionDeclaration output : step.getStepDefinition().outputs()){
                String outputName = step.getFinalStepName() + "." + output.getName();
                if (allFlowOutputs.containsKey(outputName)) {
                    outputs.add(new InputWithStepName(step.getFinalStepName(), output));
                }
                else if(allFlowOutputs.containsKey(getFlowLevelAlias(outputName))){
                    outputs.add(new InputWithStepName(step.getFinalStepName(), output));
                }
            }
        }
    }
    @Override
    public List<InputWithStepName> getOutputs() {
        return outputs;
    }
    @Override
    public String getStepName(int counter){
        String fullNameInput = flowFreeInputs.get(counter);
        String [] tmp = fullNameInput.split("\\.");
        return tmp[0];
    }

    @Override
    public List<InputWithStepName> getMandatoryInputs (){
        return mandatoryInputs;
    }

    @Override
    public List<InputWithStepName> getOptionalInputs (){
        return optionalInputs;
    }

    @Override
    public void addFlowOutput(String outputName) {
        flowOutputs.add(outputName);
    }
    @Override
    public String getUniqueId() {
        return uniqueId;
    }
    @Override
    public List<String> getFinalStepNames() {
        return finalStepNames;
    }

    @Override
    public void addFlowStep(StepUsageDeclaration stepUsageDeclaration) {
        steps.add(stepUsageDeclaration);
    }

    @Override
    public void createFreeInputOutputLists() {
        List<String> flowOutputsMayBeDeleted = new ArrayList<>();
        String preAliasKey = "";
        for (int i=0; i<steps.size(); i++){
            List<DataDefinitionDeclaration> inputStep =steps.get(i).getStepDefinition().inputs();
            for (DataDefinitionDeclaration dataInput:inputStep){
                    String key = steps.get(i).getFinalStepName() + "." + dataInput.getName();
                    if (flowLevelAlias.get(key) != null) {
                        preAliasKey = key;
                        key = flowLevelAlias.get(key);
                    }

                    if (flowFreeOutputs.contains(key)) // that means he found him
                        flowFreeOutputs.remove(key);
                    else {
                        boolean flagStr = false;
                        for (String str:flowFreeOutputs){
                            String [] tmp = str.split("\\.");
                            String[] tmp2 = key.split("\\.");
                            if (tmp[1].equals(tmp2[1]) && isDataDefEquals(tmp[0], tmp[1], dataInput.dataDefinition().getName())){
                                //flowFreeOutputs.remove(str);
                                flowOutputsMayBeDeleted.add(str);
                                flagStr = true;
                            }
                        }
                        if(!flagStr) {
                            flowFreeInputs.add(key);
                            if (!preAliasKey.equals(""))
                                preAliasFlowFreeInputs.add(preAliasKey);
                            else
                                preAliasFlowFreeInputs.add(key);
                            preAliasKey = "";
                        }
                    }


            }
            List<DataDefinitionDeclaration> outputStep = steps.get(i).getStepDefinition().outputs();
            for (DataDefinitionDeclaration dataOutput:outputStep){
                String key = steps.get(i).getFinalStepName() + "." + dataOutput.getName();
                if (flowLevelAlias.get(key) != null)
                    key = flowLevelAlias.get(key);
                if (customMapping.get(key) != null){
                    flowFreeOutputs.add(customMapping.get(key));
                }
                else
                    flowFreeOutputs.add(key);
            }
        }
        for (String str:flowOutputsMayBeDeleted){
            if (flowFreeOutputs.contains(str))
                flowFreeOutputs.remove(str);
        }
        convertInputDataFromArrayStringToDD(flowFreeInputs);
        convertOutpusDataFromArrayStringToDD(flowFreeOutputs);
    }

    @Override
    public boolean isDataDefEquals(String stepName, String outputName,String dataDefName){
        for (StepUsageDeclaration step:steps){
            if (step.getFinalStepName().equals(stepName)){
                List<DataDefinitionDeclaration> outputsOfThisStep = step.getStepDefinition().outputs();
                for (DataDefinitionDeclaration output:outputsOfThisStep){
                    String nameAfterAliasing = output.getName();
                    String checkAliasing = stepName+"."+output.getName();
                    if (flowLevelAlias.get(checkAliasing) != null){
                        checkAliasing = flowLevelAlias.get(checkAliasing);
                        String [] tmp = checkAliasing.split("\\.");
                        nameAfterAliasing = tmp[1];
                    }
                    if (nameAfterAliasing.equals(outputName) && output.dataDefinition().getName().equals(dataDefName))
                       return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> getPreAliasFlowFreeInputs() {
        return preAliasFlowFreeInputs;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }


    @Override
    public List<DataDefinitionDeclaration> getFlowFreeInputs() {
        return flowFreeInputsDataDefenition;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }
    @Override
    public String getFlowLevelAlias(String sourceDataName)
    {
        return flowLevelAlias.get(sourceDataName);
    }
    @Override
    public Map<String,String> getAllFlowLevelAlias()
    {
        return flowLevelAlias;
    }

    @Override
    public boolean isReadOnly() {
        return readonly;
    }
    @Override
    public Map<String,Class<?>> getAllInputs() {
        return allFlowInputs;
    }
    @Override
    public Map<String,Class<?>> getAllOutputs() {
        return allFlowOutputs;
    }


    @Override
    public void convertInputDataFromArrayStringToDD(List<String> inputs) {
        for (String input:inputs){
            String [] tmp = input.split("\\.");
            if(tmp.length!=2)
                return;
            for (StepUsageDeclaration step:steps){
                if (step.getFinalStepName().equals(tmp[0])){
                    for(DataDefinitionDeclaration inputDataDef:step.getStepDefinition().inputs()){
                        String inputNameAlias = step.getFinalStepName()+"."+inputDataDef.getName();
                        if (flowLevelAlias.get(inputNameAlias)!=null) {
                            inputNameAlias = flowLevelAlias.get(inputNameAlias);
                        }
                        if(input.equals(inputNameAlias)){
                            flowFreeInputsDataDefenition.add(inputDataDef);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void convertOutpusDataFromArrayStringToDD(List<String> outputs) {
        for (String output:outputs){
            String [] tmp = output.split("\\.");
            for (StepUsageDeclaration step:steps){
                if (step.getFinalStepName().equals(tmp[0])){
                    for(DataDefinitionDeclaration outputDataDef:step.getStepDefinition().outputs()){
                        String outputNameAlias = step.getFinalStepName()+"."+outputDataDef.getName();
                        if (flowLevelAlias.get(outputNameAlias)!=null) {
                            outputNameAlias = flowLevelAlias.get(outputNameAlias);
                        }
                        if(output.equals(outputNameAlias)){
                            flowFreeOutputsDataDefenition.add(outputDataDef);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> getFlowFreeInputsString() {
        return flowFreeInputs;
    }

    @Override
    public List<DataDefinitionDeclaration> getFlowFreeOutputs() {
        return flowFreeOutputsDataDefenition;
    }

    @Override
    public List<String> getFlowFreeOutputsString() {
        return flowFreeOutputs;
    }

    @Override
    public void printFreeInputs() {
        System.out.println("----INPUTS----");
        for(int i=0; i<flowFreeInputs.size(); i++){
            String[] freeInput = flowFreeInputs.get(i).split("\\.");
            System.out.println(freeInput[1]);
            System.out.println(flowFreeInputsDataDefenition.get(i).dataDefinition().getName());
            System.out.println(freeInput[0]);
            System.out.println(flowFreeInputsDataDefenition.get(i).necessity());
            System.out.println();
        }
    }

    @Override
    public void printFreeOutputs() {
        System.out.println("----OUTPUTS----");
        for(int i=0; i<flowFreeOutputs.size(); i++){
            String[] freeOutput = flowFreeOutputs.get(i).split("\\.");
            System.out.println(freeOutput[1]);
            System.out.println(flowFreeOutputsDataDefenition.get(i).dataDefinition().getName());
            System.out.println(freeOutput[0]);
            System.out.println();
        }
    }

    @Override
    public String getFlowLevelCustomMapping(String name)
    {
        String nameAfterAlias = flowLevelAlias.get(name);
        if (nameAfterAlias !=null)
            return customMapping.get(nameAfterAlias);
        else
            return customMapping.get(name);
    }
    @Override
    public Map<String,String> getCustomMapping()
    {
        return customMapping;
    }
}
