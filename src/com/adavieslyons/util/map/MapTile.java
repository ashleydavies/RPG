package com.adavieslyons.util.map;

import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.XMLParser;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Ashley
 */
public class MapTile {
	static MapTile[] tiles;

	public static MapTile getTile(int id) {
		return tiles[id];
	}
	
	public static MapTile[] getTiles() {
		return tiles;
	}

	int id;
	String name;
	private boolean collision;
	private int frameLength;
	TextureType textureType;
	Image[] textures;

	static {
		Document info = XMLParser.instance.parseXML(MapTile.class
				.getClassLoader().getResourceAsStream("data/xml/tileData.xml"));

		int tileCount = Integer.parseInt(info.getElementsByTagName("tileCount")
				.item(0).getTextContent());
		tiles = new MapTile[tileCount];

		Element tileRoot = (Element) info.getElementsByTagName("tileInfo")
				.item(0);
		NodeList tileNodes = tileRoot.getElementsByTagName("tile");
		
		// Optionals
		int frameLength = 0;

		for (int i = 0; i < tileCount; i++) {
			Element i_tileNode = (Element) tileNodes.item(i);

			int i_tileNodeID = Integer.parseInt(i_tileNode.getAttribute("id"));
			String i_tileName = i_tileNode.getElementsByTagName("name").item(0)
					.getTextContent();
			boolean i_tileCollision = (Integer
					.parseInt(i_tileNode.getElementsByTagName("collision")
							.item(0).getTextContent())) == 1 ? true : false;
			TextureType i_textureType = TextureType.valueOf(i_tileNode
					.getElementsByTagName("textureType").item(0)
					.getTextContent().toUpperCase());

			NodeList textureDataNodeList = i_tileNode.getElementsByTagName("textureData");
			if (textureDataNodeList.getLength() > 0)
			{
				Element textureData = (Element) textureDataNodeList.item(0);
				
				if (textureData.hasAttribute("frameTime"))
					frameLength = Integer.parseInt(textureData.getAttribute("frameTime"));
			}
			
			NodeList textureNodes = i_tileNode.getElementsByTagName("texture");

			Image[] textures = new Image[textureNodes.getLength()];

			for (int t = 0; t < textureNodes.getLength(); t++) {
				Element i_textureNode = (Element) textureNodes.item(t);
				int i_textureID = Integer.parseInt(i_textureNode
						.getAttribute("id"));
				int i_textureSpritesheet = Integer.parseInt(i_textureNode
						.getElementsByTagName("spritesheet").item(0)
						.getTextContent());
				int i_textureXPos = Integer.parseInt(i_textureNode
						.getElementsByTagName("xPos").item(0).getTextContent());
				int i_textureYPos = Integer.parseInt(i_textureNode
						.getElementsByTagName("yPos").item(0).getTextContent());
				textures[i_textureID] = SpriteSheet.getSpriteSheet(
						i_textureSpritesheet).getSubImage(i_textureXPos,
						i_textureYPos, 32, 32);
			}

			tiles[i_tileNodeID] = new MapTile(i_tileNodeID, i_tileName,
					i_tileCollision, i_textureType, textures, frameLength);
		}
	}

	private MapTile(int tileID, String Name, boolean Collision,
			TextureType TextureType, Image[] Textures, int frameLength) {
		id = tileID;
		name = Name;
		collision = Collision;
		textureType = TextureType;
		textures = Textures;
		this.frameLength = frameLength;
	}

	public boolean getCollision() {
		return collision;
	}
	
	public Image getBasicTexture() {
		return textures[0];
	}

	public void render(GameContainer gc, Graphics graphics, int x, int y, int totalDelta) {
		if (textureType == TextureType.STATIC)
			graphics.drawImage(textures[0], x, y);
		else if (textureType == TextureType.DYNAMIC) {
			int totalFrameLength = frameLength * textures.length;
			int frameDelta = totalDelta % totalFrameLength;
			
			for (int i = 0; i < textures.length; i++) {
				if (frameDelta >= i * frameLength && frameDelta < (i + 1) * frameLength)
					graphics.drawImage(textures[i], x, y);
			}
		}
	}
}
