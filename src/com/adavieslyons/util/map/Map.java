package com.adavieslyons.util.map;

import com.adavieslyons.orthorpg.entities.NPC;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.Vector2i;
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
 * @author Ashley
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
		for (NPC npc : npcs)
			npc.update(gc, game, delta);
	}
	
	public boolean getCollideable(int x, int y) {
		for (MapLayer layer : layers) {
			if (layer.getCollideable(y, x)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		// TODO: Don't render every individual tile every frame; clip & combine
		for (MapLayer layer : layers)
			layer.render(gc, graphics);
		
		for (NPC npc : npcs)
			npc.render(gc, graphics);
	}
	
	public void load(GameContainer gc, GameState game) throws SlickException {
		info = XMLParser.instance.parseXML(this.getClass().getClassLoader().getResourceAsStream("data/xml/map/1.xml"));
		
		name = info.getElementsByTagName("name").item(0).getTextContent();
		width = Integer.parseInt(info.getElementsByTagName("mapWidth").item(0).getTextContent());
		height = Integer.parseInt(info.getElementsByTagName("mapHeight").item(0).getTextContent());
		
		Element layerRoot = (Element) info.getElementsByTagName("tileData").item(0);
		NodeList layerNodes = layerRoot.getElementsByTagName("layer");
		
		layers = new MapLayer[layerNodes.getLength()];
		
		for (int i = 0; i < layerNodes.getLength(); i++) {
			Element i_layerNode = (Element) layerNodes.item(i);
			
			NodeList i_rowNodes = i_layerNode.getElementsByTagName("row");
			MapTileData[][] i_layerTiles = new MapTileData[height][width];
			
			for (int r = 0; r < i_rowNodes.getLength(); r++) {
				Element i_rowNode = (Element) i_rowNodes.item(r);
				
				NodeList i_colNodes = i_rowNode.getElementsByTagName("tile");
				i_layerTiles[r] = new MapTileData[width];
				
				for (int c = 0; c < i_colNodes.getLength(); c++) {
					Element i_tile = (Element) i_colNodes.item(c);
					
					int i_tileID = Integer.parseInt(i_tile.getAttribute("tileID"));
					
					i_layerTiles[r][c] = new MapTileData(i_tileID);
				}
			}
			
			layers[i] = new MapLayer(i_layerTiles);
		}
		
		Element npcRoot = (Element) info.getElementsByTagName("NPCs").item(0);
		
		NodeList npcNodes = npcRoot.getElementsByTagName("NPC");
		npcs = new NPC[npcNodes.getLength()];

		for (int i = 0; i < npcNodes.getLength(); i++) {
			Element i_npcNode = (Element) npcNodes.item(i);
			
			int i_npcTypeID = Integer.parseInt(i_npcNode.getElementsByTagName("id").item(0).getTextContent());
			
			NodeList pathContainer = i_npcNode.getElementsByTagName("path");
			
			Vector2i path[] = null;
			
			if (pathContainer.getLength() > 0) {
				Element i_pathContainer = (Element) pathContainer.item(0);
				
				NodeList pathNodes = i_pathContainer.getElementsByTagName("node");
				
				path = new Vector2i[pathNodes.getLength()];
				
				for (int p = 0; p < pathNodes.getLength(); p++)
				{
					Element i_pathNode = (Element) pathNodes.item(p);
					
					int pX = Integer.parseInt(i_pathNode.getAttribute("xPos"));
					int pY = Integer.parseInt(i_pathNode.getAttribute("yPos"));
					path[p] = new Vector2i(pX, pY);
				}
			}
			NPC npc = new NPC(gc, game, i_npcTypeID, this, path);
			npcs[i] = npc;
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
