package com.adavieslyons.util;

import org.newdawn.slick.Image;
import org.w3c.dom.Document;

public class Item {
	private int id;
	private String name;
	private Image image;
	
	public static Item[] items;
	
	static {
		// Load item data from the XML file
		Document data = XMLParser.instance.parseXML(Item.class.getClassLoader().getResourceAsStream("xml/itemData.xml"));
	}
	
	public Item(int id) {
		this.id = id;
	}
}
