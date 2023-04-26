package main;

import menu.Menu;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException
    {
        Menu menu = new Menu();
        while (true)
            menu.showMenu();
    }
}
