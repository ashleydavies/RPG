package com.adavieslyons.util;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ashley on 24/02/2016.
 * <p/>
 * This is a bit horrible, needs a refactor at some point
 * but it's a significant improvement over what we had before
 * and it's a quick fix for now.
 */
public class FileLoader {
    public static Image getImage(String imageName, String extension) {
        try {
            return new Image("resources/img/" + imageName + extension);
        } catch (SlickException e) {
            throw new Error("Failure to load image " + imageName);
        }
    }

    public static Image getImage(String imageName) {
        return getImage(imageName, ".png");
    }

    public static Document getXML(String xmlName) {
        try {
            Document document;
            InputStream in = new FileInputStream("resources/data/xml/" + xmlName + ".xml");
            document = XMLParser.INSTANCE.parseXML(in);
            in.close();
            return document;
        } catch (IOException e) {
            throw new Error("Failure to load XML file " + xmlName);
        }
    }

    public static Properties getProperties(String propertiesName) {
        try {
            Properties p = new Properties();
            InputStream in = new FileInputStream("resources/data/properties/" + propertiesName + ".properties");
            p.load(in);
            in.close();
            return p;
        } catch (IOException e) {
            throw new Error("Failed to load properties file " + propertiesName);
        }
    }
}
