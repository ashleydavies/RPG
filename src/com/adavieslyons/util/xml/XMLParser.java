package com.adavieslyons.util.xml;

import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.util.dialog.DialogAction;
import com.adavieslyons.util.dialog.DialogCondition;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.DialogReply;
import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ashley
 */
public enum XMLParser {
    INSTANCE;

    static final Properties ChatSubstitution;

    static {
        ChatSubstitution = new Properties();
        try {
            InputStream in = new FileInputStream("data/properties/NPCSubstitution.properties");
            ChatSubstitution.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
        }
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
