package main;

import java.util.Scanner;

import static dataloader.FlowLoader.loadFlowFromXmlFile;

public class Main
{
    public static void main(String[] args)
    {
        // Prompt the user for the XML file path
        System.out.println("Enter the full path to the XML file:");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();

        // Load the flow from the XML file
        loadFlowFromXmlFile(filePath);
    }
}
