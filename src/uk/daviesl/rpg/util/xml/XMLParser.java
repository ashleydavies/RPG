package uk.daviesl.rpg.util.xml;

import uk.daviesl.rpg.util.FileLoader;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Ashley
 */
public enum XMLParser {
    INSTANCE;

    static final Properties ChatSubstitution;

    static {
        ChatSubstitution = FileLoader.getProperties("NPCSubstitution");
    }

    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;

    XMLParser() {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (Exception e) {
            System.out.println("FAILED TO CREATE DOCUMENT BUILDER: " + e.getMessage());
        }
    }

    public Document parseXML(File file) {
        try {
            return dBuilder.parse(file);
        } catch (Exception e) {
            System.out.println("ERROR PARSING XML FILE");
        }

        return null;
    }

    public Document parseXML(InputStream file) {
        try {
            return dBuilder.parse(file);
        } catch (Exception e) {
            System.out.println("ERROR PARSING XML FILE");
        }

        return null;
    }
}
