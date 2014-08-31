package com.adavieslyons.util.inventory;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.XMLParser;

public class Item {
	private int id;
	private String name;
	private ItemType type;
	private Image image;
	
	private static ArrayList<Item> items;
	
	static {
		// Load item data from the XML file
		Document document = XMLParser.instance.parseXML(Item.class.getClassLoader().getResourceAsStream("xml/itemData.xml"));
		// TODO: Finish loading
		items = new ArrayList<Item>();
		
		NodeList itemNodes = document.getElementsByTagName("item");
		for (int i = 0; i < itemNodes.getLength(); i++)
		{
			Element i_itemNode = (Element) itemNodes.item(i);
			String name = i_itemNode.getAttribute("name");
			ItemType type = ItemType.valueOf(i_itemNode.getAttribute("type").toUpperCase());
			
			Element textureElem = (Element) i_itemNode.getElementsByTagName("texture");
			int spriteSheet = Integer.parseInt(textureElem.getElementsByTagName("spritesheet").item(0).getTextContent());
			int xPos = Integer.parseInt(textureElem.getElementsByTagName("xPos").item(0).getTextContent());
			int yPos = Integer.parseInt(textureElem.getElementsByTagName("yPos").item(0).getTextContent());
			Image image = SpriteSheet.getSpriteSheet(spriteSheet).getSubImage(xPos, yPos, 32, 64);
			
			Item item = new Item(i, name, type, image);
		}
	}
	
	public static Item getItem(int id) {
		return items.get(id);
	}
	
	public Item(int id, String name, ItemType type, Image image) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.image = image;
	}
}
