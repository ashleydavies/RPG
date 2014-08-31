package com.adavieslyons.util.inventory;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.w3c.dom.Document;

import com.adavieslyons.util.XMLParser;

public class Item {
	private int id;
	private String name;
	private Image image;
	
	private static ArrayList<Item> items;
	
	static {
		// Load item data from the XML file
		Document data = XMLParser.instance.parseXML(Item.class.getClassLoader().getResourceAsStream("xml/itemData.xml"));
		// TODO: Finish loading
		items = new ArrayList<Item>();
	}
	
	public static Item getItem(int id) {
		return items.get(id);
	}
	
	public Item(int id) {
		this.id = id;
	}
}
