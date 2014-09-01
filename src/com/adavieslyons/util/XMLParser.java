package com.adavieslyons.util;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * 
 * @author Ashley
 */
public final class XMLParser {
	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			.newInstance();
	static DocumentBuilder dBuilder;

	public static XMLParser instance = new XMLParser();

	private XMLParser() {
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (Exception e) {
		}
	}

	public Document parseXML(File file) {
		try {
			return dBuilder.parse(file);
		} catch (Exception e) {
		}

		return null;
	}

	public Document parseXML(InputStream file) {
		try {
			return dBuilder.parse(file);
		} catch (Exception e) {
		}

		return null;
	}
}
