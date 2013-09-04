package com.adavieslyons.util.map;

import com.adavieslyons.orthorpg.entities.NPC;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.XMLParser;
import com.adavieslyons.util.map.pathfinding.AStar;
import com.adavieslyons.util.map.pathfinding.CollisionMap;
import com.adavieslyons.util.map.pathfinding.AStar.Node;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author
 * Ashley
 */
public class Map {
    int id;
    String name;
    Document info;
    int width;
    int height;
    MapLayer layers[];
    NPC npcs[];
    CollisionMap collisionMap;
    Node[][] nodeMatrix;
    
    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        for (NPC npc: npcs)
            npc.update(gc, game, delta);
    }
    
    public boolean getCollideable(int y, int x) {
        for (MapLayer layer: layers) {
            if (layer.getCollideable(y, x)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        // TODO: Don't render every individual tile every frame; clip & combine
        for (MapLayer layer: layers)
            layer.render(gc, graphics);
        
        for (NPC npc: npcs)
            npc.render(gc, graphics);
    }
    
    public void load(GameContainer gc, GameState game) throws SlickException
    {
        info = XMLParser.instance.parseXML(this.getClass().getClassLoader().getResourceAsStream("data/xml/map/1.xml"));
        
        name = info.getElementsByTagName("name").item(0).getTextContent();
        width = Integer.parseInt(info.getElementsByTagName("mapWidth").item(0).getTextContent());
        height = Integer.parseInt(info.getElementsByTagName("mapHeight").item(0).getTextContent());
        
        Element layerRoot = (Element)info.getElementsByTagName("tileData").item(0);
        NodeList layerNodes = layerRoot.getElementsByTagName("layer");
        
        layers = new MapLayer[layerNodes.getLength()];
        
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Element i_layerNode = (Element)layerNodes.item(i);
            
            int i_layerNodeID = Integer.parseInt(i_layerNode.getAttribute("id"));
            
            NodeList i_rowNodes = i_layerNode.getElementsByTagName("row");
            MapTileData[][] i_layerTiles = new MapTileData[height][width];
            
            for (int r = 0; r < i_rowNodes.getLength(); r++) {
                Element i_rowNode = (Element)i_rowNodes.item(r);
                
                int i_rowNodeID = Integer.parseInt(i_rowNode.getAttribute("id"));
                
                NodeList i_colNodes = i_rowNode.getElementsByTagName("tile");
                i_layerTiles[i_rowNodeID] = new MapTileData[width];
                
                for (int c = 0; c < i_colNodes.getLength(); c++) {
                    Element i_tile = (Element)i_colNodes.item(c);
                    
                    int i_colID = Integer.parseInt(i_tile.getAttribute("col"));
                    int i_tileID = Integer.parseInt(i_tile.getAttribute("tileID"));
                    
                    i_layerTiles[i_rowNodeID][i_colID] = new MapTileData(i_tileID);
                }
            }
            
            layers[i_layerNodeID] = new MapLayer(i_layerTiles);
        }
        
        Element entityRoot = (Element)info.getElementsByTagName("NPCs").item(0);
        
        NodeList npcNodes = entityRoot.getElementsByTagName("NPC");
        npcs = new NPC[npcNodes.getLength()];
        
        for (int i = 0; i < npcNodes.getLength(); i++) {
            Element i_npcNode = (Element)npcNodes.item(i);
            
            int i_npcID = Integer.parseInt(i_npcNode.getAttribute("id"));
            int i_npcTypeID = Integer.parseInt(i_npcNode.getElementsByTagName("id").item(0).getTextContent());
            
            npcs[i_npcID] = new NPC(gc, game, i_npcTypeID, this);
        }
        
        collisionMap = new CollisionMap(this);
        nodeMatrix = AStar.getNodeMatrix(collisionMap);
    }
    
    public Node[][] getNodeMatrix() {
    	return nodeMatrix;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
}
