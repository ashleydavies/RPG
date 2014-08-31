package com.adavieslyons.util.map;

import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.entities.NPC;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.XMLParser;
import com.adavieslyons.util.map.pathfinding.AStar;
import com.adavieslyons.util.map.pathfinding.AStar.Node;
import com.adavieslyons.util.map.pathfinding.CollisionMap;

/**
 * 
 * @author Ashley
 */
public class Map {
	int id;
	GameState game;
	String name;
	Document info;
	int width;
	int height;
	MapLayer layers[];
	boolean fogOfWar[][];
	NPC npcs[];
	CollisionMap collisionMap;
	Node[][] nodeMatrix;
	Image fogOfWarTexture;
	
	Vector2i offset = new Vector2i(128, 128);
	
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		for (NPC npc : npcs)
			npc.update(gc, game, delta);
		
		// Move with arrow keys
		if (game.getInput().isKeyDown(Input.KEY_LEFT))
			addOffset(1, 0);
		if (game.getInput().isKeyDown(Input.KEY_RIGHT))
			addOffset(-1, 0);
		
		if (game.getInput().isKeyDown(Input.KEY_UP))
			addOffset(0, 1);
		if (game.getInput().isKeyDown(Input.KEY_DOWN))
			addOffset(0, -1);
	}
	
	public void addOffset(int x, int y)
	{
		addOffset(new Vector2i(x, y));
	}
	
	public void addOffset(Vector2i offset)
	{
		setOffset(this.offset.add(offset));
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
	
	public void renderPostEntities(GameContainer gc, Graphics graphics) throws SlickException {
		int tX = 0;
		for (boolean fogOfWarRow[] : fogOfWar)
		{
			int tY = 0;
			for (boolean fogOfWarTile : fogOfWarRow)
			{
				if (fogOfWarTile == true)
				{
					Vector2i position = tileCoordinatesToGameCoordinates(tX, tY);
					fogOfWarTexture.draw(position.getX(), position.getY());
				}
				tY++;
			}
			tX++;
		}
	}
	
	// SCREEN => TILE
	public Vector2i screenCoordinatesToTileCoordinates(int x, int y) {
		return new Vector2i(
				(int) Math.floor((x - offset.getX()) / Game.TILE_SIZE),
				(int) Math.floor((y - offset.getY()) / Game.TILE_SIZE));
	}
	
	// SCREEN => TILE
	public Vector2i screenCoordinatesToTileCoordinates(Vector2i coordinates) {
		return screenCoordinatesToTileCoordinates(coordinates.getX(), coordinates.getY());
	}
	
	// TILE => GAME
	public Vector2i tileCoordinatesToGameCoordinates(int x, int y) {
		return new Vector2i(x * Game.TILE_SIZE + offset.getX(), y * Game.TILE_SIZE + offset.getY());
	}
	
	// TILE => GAME
	public Vector2i tileCoordinatesToGameCoordinates(Vector2i tileCoordinates) {
		return tileCoordinatesToGameCoordinates(tileCoordinates.getX(), tileCoordinates.getY());
	}
	
	// SCREEN => GAME
	public Vector2i screenCoordinatesToGameCoordinates(Vector2i screenCoordinates) {
		return screenCoordinates.add(offset);
	}
	
	// SCREEN => GAME
	public Vector2i screenCoordinatesToGameCoordinates(Vector2f renderPosition) {
		return screenCoordinatesToGameCoordinates(new Vector2i((int) renderPosition.getX(), (int) renderPosition.getY()));
	}
	
	// GAME => SCREEN
	public Vector2i gameCoordinatesToScreenCoordinates(Vector2i gameCoordinates) {
		return gameCoordinates.subtract(offset);
	}
	
	public void load(GameContainer gc, GameState game) throws SlickException {
		this.game = game;
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
			
			layers[i] = new MapLayer(i_layerTiles, this);
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
		
		fogOfWarTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0, 0, Game.TILE_SIZE, Game.TILE_SIZE);
		fogOfWar = new boolean[getWidth()][getHeight()];
		for (boolean row[] : fogOfWar)
			Arrays.fill(row, true);
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
	
	public boolean isFogOfWar(int tX, int tY) {
		return fogOfWar[tX][tY];
	}

	public void revealCoordinate(int tX, int tY) {
		fogOfWar[tX][tY] = false;
	}
	
	public Vector2i getOffset() {
		return offset;
	}

	public void setOffset(Vector2i offset) {
		if (offset.getX() > 150)
			offset.setX(150);
		if (offset.getY() > 150)
			offset.setY(150);
		
		if (width * Game.TILE_SIZE + offset.getX() < game.WIDTH - 150)
			offset.setX(game.WIDTH - 150 - width * Game.TILE_SIZE);

		if (height * Game.TILE_SIZE + offset.getY() < game.HEIGHT - 150)
			offset.setY(game.HEIGHT - 150 - height * Game.TILE_SIZE);
		
		this.offset = offset;
	}
}
