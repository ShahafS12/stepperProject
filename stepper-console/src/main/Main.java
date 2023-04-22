package main;

import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static dataloader.FlowLoader.loadFlowFromXmlFile;

public class Main
{
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException
    {
        menu.Menu.showMenu();
    }
}
