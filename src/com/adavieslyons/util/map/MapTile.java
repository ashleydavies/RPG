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

		Element tileRoot = (Element) info.getElementsByTagName("tileInfo")
				.item(0);
		NodeList tileNodes = tileRoot.getElementsByTagName("tile");
		tiles = new MapTile[tileNodes.getLength()];

		for (int i = 0; i < tileNodes.getLength(); i++) {
			Element i_tileNode = (Element) tileNodes.item(i);

			String name = i_tileNode.getAttribute("name");
			boolean collision = (Integer.parseInt(i_tileNode
					.getAttribute("collision")) == 1 ? true : false);
			TextureType textureType = TextureType.STATIC;
			int frameLength = 0;

			NodeList textureDataNodeList = i_tileNode
					.getElementsByTagName("textureData");
			if (textureDataNodeList.getLength() > 0) {
				Element textureData = (Element) textureDataNodeList.item(0);

				if (textureData.hasAttribute("type"))
					textureType = TextureType.valueOf(textureData.getAttribute(
							"type").toUpperCase());
				if (textureData.hasAttribute("frameTime"))
					frameLength = Integer.parseInt(textureData
							.getAttribute("frameTime"));
			}

			NodeList textureNodes = i_tileNode.getElementsByTagName("texture");

			Image[] textures = new Image[textureNodes.getLength()];

			for (int t = 0; t < textureNodes.getLength(); t++) {
				Element i_textureNode = (Element) textureNodes.item(t);

				int spritesheet = Integer.parseInt(i_textureNode
						.getAttribute("spritesheet"));
				int xPos = Integer.parseInt(i_textureNode.getAttribute("xPos"));
				int yPos = Integer.parseInt(i_textureNode.getAttribute("yPos"));

				textures[t] = SpriteSheet.getSpriteSheet(spritesheet)
						.getSubImage(xPos, yPos, 32, 32);
			}

			tiles[i] = new MapTile(i, name, collision, textureType, textures,
					frameLength);
		}
	}

	private MapTile(int tileID, String Name, boolean Collision,
			TextureType TextureType, Image[] Textures, int frameLength) {
		System.out.println(Name);
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

	public void render(GameContainer gc, Graphics graphics, int x, int y,
			int totalDelta) {
		if (textureType == TextureType.STATIC)
			graphics.drawImage(textures[0], x, y);
		else if (textureType == TextureType.DYNAMIC) {
			int totalFrameLength = frameLength * textures.length;
			int frameDelta = totalDelta % totalFrameLength;

			for (int i = 0; i < textures.length; i++) {
				if (frameDelta >= i * frameLength
						&& frameDelta < (i + 1) * frameLength)
					graphics.drawImage(textures[i], x, y);
			}
		}
	}
}
