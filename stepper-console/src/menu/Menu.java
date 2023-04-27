package menu;

import exit.ExitProgram;
import menu.caseimpl.LoadXMLFile;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.stepper.StepperDefinition;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu
{
    private StepperDefinition stepper;
    public void showMenu() throws IOException, ParserConfigurationException, SAXException {
        System.out.println("1. Load flow from XML file");
        System.out.println("2. Show flow statistics");
        System.out.println("3. Show flow steps");
        System.out.println("4. Show flow formal outputs");
        System.out.println("5. Show flow free inputs");
        System.out.println("6. Execute flow");
        System.out.println("7. Exit");
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
        switch (action)
        {
            case 1:
                LoadXMLFile loadXMLFile = new LoadXMLFile();
                stepper =  loadXMLFile.loadXMLFile();
                break;
            case 2:
                // Show flow statistics
                break;
            case 3:
                // Show flow steps
                break;
            case 4:
                // Show flow formal outputs
                break;
            case 5:
                // Show flow free inputs
                break;
            case 6:
                //showFlowNames();
                if(stepper != null)
                {
                    FLowExecutor fLowExecutor = new FLowExecutor();

                    FlowExecution flow3Execution1 = new FlowExecution("2", stepper.getFlowDefinition("Rename Files"));
                    fLowExecutor.executeFlow(flow3Execution1);
                }
                else
                {
                    System.out.println("Please load a flow first");
                }

                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }
}
