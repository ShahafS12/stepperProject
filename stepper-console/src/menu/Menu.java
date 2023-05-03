package menu;

import exit.ExitProgram;
import menu.caseimpl.LoadXMLFile;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.StepDefinition;
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
    private List<FlowExecutionStatistics> stats = new ArrayList<>();

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
                case 1:
                    LoadXMLFile loadXMLFile = new LoadXMLFile();
                    stepper = (loadXMLFile.loadXMLFile());
                    flowNames.addAll(stepper.getFlowNames());//add the latest names of the flows
                    //flowIdMap.put(flowNames.get(flowNames.size()-1),uniqueFlowIdCounter);
                    for (int i = 0; i < flowNames.size(); i++) {
                        flowExecutionMap.put(uniqueFlowIdCounter, new FlowExecution(flowNames.get(i), stepper.getFlowDefinition(flowNames.get(i))));
                        uniqueFlowIdCounter++;
                    }
                    break;
                case 2:
                    // Show flows
                    System.out.println("Choose a flow: ");
                    System.out.println("0: return to main menu "); //TODO: 0 return to Main Menu
                    for (int i = 0; i < flowNames.size(); i++) {
                        System.out.println(i + 1 + ": " + flowNames.get(i));
                    }
                    System.out.println();
                    actionFlow = scanner.nextInt();
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
                    flow.validateFlowStructure(); // TODO: validate needs to be somewhere else
                    flow.printFreeInputs();
                    flow.printFreeOutputs();

                    break;
                case 3:
                    // Execute flow
                    System.out.println("Choose a flow: ");
                    System.out.println("0: return to main menu ");
                    for (int i = 0; i < flowNames.size(); i++) {
                        System.out.println(i + 1 + ": " + flowNames.get(i));
                    }
                    System.out.println();
                    actionFlow = scanner.nextInt();
                    FlowDefinition chosenFlow = stepper.getFlowDefinition(flowNames.get(actionFlow - 1));
                    FLowExecutor fLowExecutor = new FLowExecutor();
                    stats.add(fLowExecutor.executeFlow(flowExecutionMap.get(actionFlow - 1)));
                    break;
                case 4:
                    // Show past runs details
                    System.out.println("Choose a flow: ");
                    System.out.println("0: return to main menu ");
                    for (int i = 0; i < flowNames.size(); i++) {
                        System.out.println(i + 1 + ": " + flowNames.get(i));
                    }
                    System.out.println();
                    actionFlow = scanner.nextInt();
                    FlowExecutionStatistics flowExecutionStatistics = stats.get(actionFlow - 1);
                    flowExecutionStatistics.printStatistics();
                    break;
                case 5:
                    // Show flow free inputs
                    break;
                case 6:
                    // Exit
                    ExitProgram exitProgram = new ExitProgram();
                    exitProgram.exit();
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
}
