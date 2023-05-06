package menu;

import exit.ExitProgram;
import menu.caseimpl.LoadXMLFile;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class Menu
{
    private MenuVariables menuVariables = new MenuVariables();

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
        for (Integer key : menuVariables.getStats().keySet()) {
            FlowExecutionStatistics value = menuVariables.getStats().get(key);
            System.out.println(key + ") " + value.getFlowName() + " - " + value.getStartTime());
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowExecutionStatistics flowExecutionStatistics = menuVariables.getStats().get(actionFlow);
        flowExecutionStatistics.printStatistics();
    }

    private void loadStepperFromXml() throws IOException, ParserConfigurationException, SAXException
    {
        try {
            MenuVariables newMenuVariables = new MenuVariables();
            LoadXMLFile loadXMLFile = new LoadXMLFile();
            StepperDefinition newStepper = (loadXMLFile.loadXMLFile());
            newMenuVariables.setStepper(newStepper);
            ArrayList<String> newFlowNames = new ArrayList<>();
            newFlowNames.addAll(newStepper.getFlowNames());//add the latest names of the flows
            newMenuVariables.setFlowNames(newFlowNames);
            //flowIdMap.put(flowNames.get(flowNames.size()-1),uniqueFlowIdCounter);
            for (int i = 0; i < newMenuVariables.getFlowNames().size(); i++) {
                newMenuVariables.putFlowExecutionMap(newMenuVariables.getUniqueFlowIdCounter(),
                        new FlowExecution(newMenuVariables.getFlowNames().get(i), newStepper.getFlowDefinition(newFlowNames.get(i))));
                newMenuVariables.upuniqueFlowIdCounter();
            }
            this.menuVariables = newMenuVariables;
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
        for (int i = 0; i < menuVariables.getFlowNames().size(); i++) {
            System.out.println(i + 1 + ": " + menuVariables.getFlowNames().get(i));
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowDefinition chosenFlow = menuVariables.getStepper().getFlowDefinition(menuVariables.getFlowNames().get(actionFlow - 1));
        FLowExecutor fLowExecutor = new FLowExecutor();
        menuVariables.getStats().put(menuVariables.getUniqueFlowExecutionIdCounter(),fLowExecutor.executeFlow(menuVariables.getFlowExecutionMap().get(actionFlow - 1)));
        menuVariables.upuniqueFlowExecutionIdCounter();
        if(menuVariables.getFlowExecutionsStatisticsMap().containsKey(chosenFlow.getName()))
        {
            menuVariables.getFlowExecutionsStatisticsMap().get(chosenFlow.getName()).addFlowExecutionStatistics(menuVariables.getStats().get(menuVariables.getUniqueFlowExecutionIdCounter()-1));
        }
        else
        {
            FlowExecutionsStatistics flowExecutionsStatistics = new FlowExecutionsStatistics(chosenFlow.getName());
            flowExecutionsStatistics.addFlowExecutionStatistics(menuVariables.getStats().get(menuVariables.getUniqueFlowExecutionIdCounter()-1));
            menuVariables.getFlowExecutionsStatisticsMap().put(chosenFlow.getName(),flowExecutionsStatistics);
        }
    }

    private void showFlow(Scanner scanner)
    {
        int actionFlow;
        System.out.println("Choose a flow: ");
        System.out.println("0: return to main menu ");
        for (int i = 0; i < menuVariables.getFlowNames().size(); i++) {
            System.out.println(i + 1 + ": " + menuVariables.getFlowNames().get(i));
        }
        System.out.println();
        actionFlow = scanner.nextInt();
        if(actionFlow == 0)
        {
            return;
        }
        FlowDefinition flow = menuVariables.getStepper().getFlowDefinition(menuVariables.getFlowNames().get(actionFlow - 1));
        System.out.println(flow.getName());
        System.out.println(flow.getDescription());
        System.out.println(flow.getFlowFormalOutputs());
        System.out.println("The flow read-only: "+flow.isReadOnly());
        List<StepUsageDeclaration> steps = flow.getFlowSteps();
        System.out.println("\nSteps: ");
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
        for (int i = 0; i < menuVariables.getFlowNames().size(); i++) {
            System.out.println(i + 1 + ": " + menuVariables.getFlowNames().get(i));
        }
        System.out.println();
        int actionFlow = scanner.nextInt();
        if(actionFlow == 0)
            return;
        FlowDefinition flow = menuVariables.getStepper().getFlowDefinition(menuVariables.getFlowNames().get(actionFlow - 1));
        FlowExecutionsStatistics flowExecutionsStatistics = menuVariables.getFlowExecutionsStatisticsMap().get(flow.getName());
        flowExecutionsStatistics.printStatistics();
    }
}
