package menu;

import exit.ExitProgram;
import menu.caseimpl.LoadXMLFile;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class Menu
{
    private StepperDefinition stepper;
    private ArrayList<String> flowNames = new ArrayList<>();
    private Integer uniqueFlowIdCounter = 0;
    private Map<Integer,FlowExecution> flowExecutionMap = new HashMap<>();
    private Map<Integer,FlowExecutionStatistics> stats = new HashMap<>();
    private Integer uniqueFlowExecutionIdCounter = 1;
    private Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsMap = new HashMap<>();

    public void showMenu() throws IOException, ParserConfigurationException, SAXException {
        System.out.println("1. Load flow from XML file");
        System.out.println("2. Show flow");
        System.out.println("3. Execute flow");
        System.out.println("4. Show past runs details");
        System.out.println("5. Show statistics");
        System.out.println("6. Exit");
        Scanner scanner = new Scanner(System.in);
        int action = 0;
        try {
            action = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            showMenu(); // call the same function recursively to prompt the user again
            return; // exit the current function call
        }
        prefromAction(action);
    }

    private void prefromAction(int action) throws IOException, ParserConfigurationException, SAXException
    {
        Scanner scanner = new Scanner(System.in);
        int actionFlow = 0;
        try {
            switch (action) {
                case 1:// Load flow from XML file
                    loadStepperFromXml();
                    break;
                case 2:// Show flows
                    showFlow(scanner);
                    break;
                case 3:// Execute flow
                    executeFlow(scanner);
                    break;
                case 4:// Show past runs details
                    showPastRun(scanner);
                    break;
                case 5:// Show statistics
                    showStatistics(scanner);
                    break;
                case 6:// Exit
                    ExitProgram();
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println("Invalid action");
        }
        catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            return; // exit the current function call
        }
    }

    private static void ExitProgram()
    {
        ExitProgram exitProgram = new ExitProgram();
        exitProgram.exit();
    }

    private void showPastRun(Scanner scanner)
    {
        int actionFlow;
        System.out.println("Choose a flow: ");
        System.out.println("0: return to main menu ");
        for (Integer key : stats.keySet()) {
            FlowExecutionStatistics value = stats.get(key);
            System.out.println(key + ") " + value.getFlowName() + " - " + value.getStartTime());
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowExecutionStatistics flowExecutionStatistics = stats.get(actionFlow);
        flowExecutionStatistics.printStatistics();
    }

    private void loadStepperFromXml() throws IOException, ParserConfigurationException, SAXException
    {
        try {
            LoadXMLFile loadXMLFile = new LoadXMLFile();
            stepper = (loadXMLFile.loadXMLFile());
            flowNames.addAll(stepper.getFlowNames());//add the latest names of the flows
            //flowIdMap.put(flowNames.get(flowNames.size()-1),uniqueFlowIdCounter);
            for (int i = 0; i < flowNames.size(); i++) {
                flowExecutionMap.put(uniqueFlowIdCounter, new FlowExecution(flowNames.get(i), stepper.getFlowDefinition(flowNames.get(i))));
                uniqueFlowIdCounter++;
            }
        }
        catch (RuntimeException e)
        {
            System.out.println("Invalid XML file"+e.getMessage());
        }
    }

    private void executeFlow(Scanner scanner)
    {
        int actionFlow;
        System.out.println("Choose a flow: ");
        System.out.println("0: return to main menu ");
        for (int i = 0; i < flowNames.size(); i++) {
            System.out.println(i + 1 + ": " + flowNames.get(i));
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowDefinition chosenFlow = stepper.getFlowDefinition(flowNames.get(actionFlow - 1));
        FLowExecutor fLowExecutor = new FLowExecutor();
        stats.put(uniqueFlowExecutionIdCounter,fLowExecutor.executeFlow(flowExecutionMap.get(actionFlow - 1)));
        uniqueFlowExecutionIdCounter++;
        if(flowExecutionsStatisticsMap.containsKey(chosenFlow.getName()))
        {
            flowExecutionsStatisticsMap.get(chosenFlow.getName()).addFlowExecutionStatistics(stats.get(uniqueFlowExecutionIdCounter-1));
        }
        else
        {
            FlowExecutionsStatistics flowExecutionsStatistics = new FlowExecutionsStatistics(chosenFlow.getName());
            flowExecutionsStatistics.addFlowExecutionStatistics(stats.get(uniqueFlowExecutionIdCounter-1));
            flowExecutionsStatisticsMap.put(chosenFlow.getName(),flowExecutionsStatistics);
        }
    }

    private void showFlow(Scanner scanner)
    {
        int actionFlow;
        System.out.println("Choose a flow: ");
        System.out.println("0: return to main menu ");
        for (int i = 0; i < flowNames.size(); i++) {
            System.out.println(i + 1 + ": " + flowNames.get(i));
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowDefinition flow = stepper.getFlowDefinition(flowNames.get(actionFlow - 1));
        System.out.println(flow.getName());
        System.out.println(flow.getDescription());
        System.out.println(flow.getFlowFormalOutputs());
        System.out.println("The flow read-only: "+flow.isReadOnly());
        List<StepUsageDeclaration> steps = flow.getFlowSteps();
        for (int i = 0; i < steps.size(); i++) {
            StepUsageDeclaration step = steps.get(i);
            if (step.getStepName() != step.getFinalStepName())
                System.out.println(step.getStepName() + "," + step.getFinalStepName());
            else
                System.out.println(step.getStepName());
            System.out.println("Is readOnly: "+step.getStepDefinition().isReadonly());
            System.out.println();
        }
        flow.printFreeInputs();
        flow.printFreeOutputs();
    }
    private void showStatistics(Scanner scanner){
        System.out.println("Choose a flow: ");
        System.out.println("0: return to main menu ");
        for (int i = 0; i < flowNames.size(); i++) {
            System.out.println(i + 1 + ": " + flowNames.get(i));
        }
        System.out.println();
        int actionFlow = scanner.nextInt();
        if(actionFlow == 0)
            return;
        FlowDefinition flow = stepper.getFlowDefinition(flowNames.get(actionFlow - 1));
        FlowExecutionsStatistics flowExecutionsStatistics = flowExecutionsStatisticsMap.get(flow.getName());
        flowExecutionsStatistics.printStatistics();
    }
}
