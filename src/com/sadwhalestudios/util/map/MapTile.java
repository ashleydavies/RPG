package com.sadwhalestudios.util.map;

import com.sadwhalestudios.util.SpriteSheet;
import com.sadwhalestudios.util.XMLParser;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author
 * Ashley
 */
public class MapTile {
    static MapTile[] tiles;
    
    public static MapTile getTile(int id)
    {
        return tiles[id];
    }
    
    int id;
    String name;
    private boolean collision;
    TextureType textureType;
    Image[] textures;
    
    static
    {
        Document info = XMLParser.instance.parseXML(MapTile.class.getClassLoader().getResourceAsStream("data/xml/tileData.xml"));
        
        int tileCount = Integer.parseInt(info.getElementsByTagName("tileCount").item(0).getTextContent());
        tiles = new MapTile[tileCount];
        
        Element tileRoot = (Element)info.getElementsByTagName("tileInfo").item(0);
        NodeList tileNodes = tileRoot.getElementsByTagName("tile");
        
        for (int i = 0; i < tileCount; i++)
        {
            Element i_tileNode = (Element)tileNodes.item(i);
            
            int i_tileNodeID = Integer.parseInt(i_tileNode.getAttribute("id"));
            String i_tileName = i_tileNode.getElementsByTagName("name").item(0).getTextContent();
            boolean i_tileCollision = (Integer.parseInt(i_tileNode.getElementsByTagName("collision").item(0).getTextContent())) == 1 ? true : false;
            TextureType i_textureType = TextureType.valueOf(i_tileNode.getElementsByTagName("textureType").item(0).getTextContent().toUpperCase());
            
            NodeList textureNodes = i_tileNode.getElementsByTagName("texture");
            
            Image[] textures = new Image[textureNodes.getLength()];
            
            for (int t = 0; t < textureNodes.getLength(); t++)
            {
                Element i_textureNode = (Element)textureNodes.item(t);
                int i_textureID = Integer.parseInt(i_textureNode.getAttribute("id"));
                int i_textureSpritesheet = Integer.parseInt(i_textureNode.getElementsByTagName("spritesheet").item(0).getTextContent());
                int i_textureXPos = Integer.parseInt(i_textureNode.getElementsByTagName("xPos").item(0).getTextContent());
                int i_textureYPos = Integer.parseInt(i_textureNode.getElementsByTagName("yPos").item(0).getTextContent());
                textures[i_textureID] = SpriteSheet.getSpriteSheet(i_textureSpritesheet).getSubImage(i_textureXPos, i_textureYPos, 32, 32);
            }
            
            tiles[i_tileNodeID] = new MapTile(i_tileNodeID, i_tileName, i_tileCollision, i_textureType, textures);
        }
    }
    
    private MapTile(int tileID, String Name, boolean Collision, TextureType TextureType, Image[] Textures)
    {
        id = tileID;
        name = Name;
        collision = Collision;
        textureType = TextureType;
        textures = Textures;
    }
    
    public boolean getCollision()
    {
        return collision;
    }
    
    public void render(GameContainer gc, Graphics graphics, int x, int y)
    {
        graphics.drawImage(textures[0], x, y); // TODO: Support different TextureTypes other than static!
    }
}
