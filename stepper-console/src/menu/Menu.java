package menu;

import menu.caseimpl.LoadXMLFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

public class Menu
{
    public void showMenu() throws IOException, ParserConfigurationException, SAXException
    {
        System.out.println("1. Load flow from XML file");
        System.out.println("2. Show flow statistics");
        System.out.println("3. Show flow steps");
        System.out.println("4. Show flow formal outputs");
        System.out.println("5. Show flow free inputs");
        System.out.println("6. Exit");
        Scanner scanner = new Scanner(System.in);
        int action = scanner.nextInt();
        prefromAction(action);
    }

    private void prefromAction(int action) throws IOException, ParserConfigurationException, SAXException
    {
        switch (action)
        {
            case 1:
                LoadXMLFile loadXMLFile = new LoadXMLFile();
                loadXMLFile.loadXMLFile();
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
                // Exit
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
    }
}
