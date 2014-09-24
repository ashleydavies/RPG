package com.adavieslyons.util.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import com.adavieslyons.orthorpg.entities.EntityManager;
import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.orthorpg.entities.MovingEntity;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.TileSelectorGUI;
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
	EntityManager entityManager;
	GameState game;
	Document info;
	int width = -1;
	int height;
	MapLayer layers[];
	boolean fogOfWar[][];
	boolean occupiedTiles[][];
	CollisionMap collisionMap;
	Node[][] nodeMatrix;
	Image fogOfWarTexture;
	Image mapBorderTexture;
	TileSelectorGUI tsGUI;
	int tileEditingTile = 0;
	boolean tsGUIOpen;
	boolean editing;
	int totalDelta;

	Vector2i offset = new Vector2i(128, 128);

	public void update(GameContainer gc, GameState game, int delta)
			throws SlickException {
		
		totalDelta += delta;
		
		if (getEditing()) {
			// Process map editor logic
			Vector2i mouseTile = screenCoordinatesToTileCoordinates(new Vector2i(
					game.getInput().getMouseX(), game.getInput().getMouseY()));
			game.getInput();
			if (game.getInput().isKeyDown(Input.KEY_S))
				tsGUIOpen = true;

			if (tsGUIOpen && tsGUI.getTileSelected()) {
				tileEditingTile = tsGUI.getSelectedTile();
				tsGUIOpen = false;
				tsGUI.reset();
			} else if (tsGUIOpen) {
				tsGUI.update(gc, game, delta);
			} else {
				if (game.getInput().isMouseButtonDown(0)
						&& mouseTile.getX() >= 0 && mouseTile.getY() >= 0
						&& mouseTile.getX() < width
						&& mouseTile.getY() < height)
					setTile(mouseTile.getX(), mouseTile.getY(),
							tileEditingTile, 0);
			}
		}

		// Move with arrow keys
		if (game.getInput().isKeyDown(Input.KEY_LEFT))
			addOffset(1 * delta, 0);
		if (game.getInput().isKeyDown(Input.KEY_RIGHT))
			addOffset(-1 * delta, 0);

		if (game.getInput().isKeyDown(Input.KEY_UP))
			addOffset(0, 1 * delta);
		if (game.getInput().isKeyDown(Input.KEY_DOWN))
			addOffset(0, -1 * delta);
	}

	public void addOffset(int x, int y) {
		addOffset(new Vector2i(x, y));
	}

	public void addOffset(Vector2i offset) {
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

	public void render(GameContainer gc, Graphics graphics)
			throws SlickException {
		// TODO: Don't render every individual tile every frame; clip & combine
		for (MapLayer layer : layers)
			layer.render(gc, graphics, totalDelta);
		// Only render mobs if not editing
		if (getEditing() && tsGUIOpen)
			tsGUI.render(gc, graphics);
	}

	public void renderPostEntities(GameContainer gc, Graphics graphics)
			throws SlickException {
		if (!editing) {
			Vector2i screenTL = screenCoordinatesToTileCoordinates(0, 0);
			Vector2i screenBR = screenCoordinatesToTileCoordinates(
					gc.getWidth(), gc.getHeight());

			screenTL.add(new Vector2i(2, 2));
			screenBR.add(new Vector2i(1, 1));

			if (screenTL.getX() < 0)
				screenTL.setX(0);
			if (screenTL.getY() < 0)
				screenTL.setY(0);
			if (screenBR.getX() > width)
				screenBR.setX(width);
			if (screenBR.getY() > height)
				screenBR.setY(height);

			for (int y = screenTL.getY(); y < screenBR.getY(); y++) {
				for (int x = screenTL.getX(); x < screenBR.getX(); x++) {
					if (fogOfWar[x][y]) {
						Vector2i renderPosition = tileCoordinatesToGameCoordinates(
								x, y);
						fogOfWarTexture.draw(renderPosition.getX(),
								renderPosition.getY());
					}
				}
			}
		}
	}

	// SCREEN => TILE
	public Vector2i screenCoordinatesToTileCoordinates(int x, int y) {
		return new Vector2i((int) Math.floor((x - offset.getX())
				/ Game.TILE_SIZE), (int) Math.floor((y - offset.getY())
				/ Game.TILE_SIZE));
	}

	// SCREEN => TILE
	public Vector2i screenCoordinatesToTileCoordinates(Vector2i coordinates) {
		return screenCoordinatesToTileCoordinates(coordinates.getX(),
				coordinates.getY());
	}

	// TILE => GAME
	public Vector2i tileCoordinatesToGameCoordinates(int x, int y) {
		return new Vector2i(x * Game.TILE_SIZE + offset.getX(), y
				* Game.TILE_SIZE + offset.getY());
	}

	// TILE => GAME
	public Vector2i tileCoordinatesToGameCoordinates(Vector2i tileCoordinates) {
		return tileCoordinatesToGameCoordinates(tileCoordinates.getX(),
				tileCoordinates.getY());
	}

	// SCREEN => GAME
	public Vector2i screenCoordinatesToGameCoordinates(
			Vector2i screenCoordinates) {
		return screenCoordinates.add(offset);
	}

	// SCREEN => GAME
	public Vector2i screenCoordinatesToGameCoordinates(Vector2f renderPosition) {
		return screenCoordinatesToGameCoordinates(new Vector2i(
				(int) renderPosition.getX(), (int) renderPosition.getY()));
	}

	// GAME => SCREEN
	public Vector2i gameCoordinatesToScreenCoordinates(Vector2i gameCoordinates) {
		return gameCoordinates.subtract(offset);
	}

	public void load(GameContainer gc, GameState game, int mapID, EntityManager entityManager)
			throws SlickException {
		this.game = game;
		this.id = mapID;
		info = XMLParser.instance.parseXML(this.getClass().getClassLoader()
				.getResourceAsStream("data/xml/map/" + mapID + ".xml"));

		Element layerRoot = (Element) info.getElementsByTagName("tileData")
				.item(0);
		NodeList layerNodes = layerRoot.getElementsByTagName("layer");

		layers = new MapLayer[layerNodes.getLength()];

		for (int i = 0; i < layerNodes.getLength(); i++) {
			Element layerNode = (Element) layerNodes.item(i);

			NodeList rowNodes = layerNode.getElementsByTagName("row");

			height = rowNodes.getLength();

			if (width == -1) {
				Element rowNode0 = (Element) rowNodes.item(0);
				width = rowNode0.getElementsByTagName("tile").getLength();
			}

			MapTileData[][] layerTiles = new MapTileData[height][width];

			for (int r = 0; r < height; r++) {
				Element rowNode = (Element) rowNodes.item(r);

				NodeList colNodes = rowNode.getElementsByTagName("tile");
				layerTiles[r] = new MapTileData[width];

				for (int c = 0; c < width; c++) {
					Element tile = (Element) colNodes.item(c);

					int tileID = Integer.parseInt(tile.getAttribute("tileID"));

					layerTiles[r][c] = new MapTileData(tileID);
				}
			}

			layers[i] = new MapLayer(layerTiles, this);
		}

		Element mobRoot = (Element) info.getElementsByTagName("mobs").item(0);
		if (mobRoot != null) {
			NodeList mobNodes = mobRoot.getElementsByTagName("mob");
			
			for (int i = 0; i < mobNodes.getLength(); i++) {
				Element mobNode = (Element) mobNodes.item(i);
	
				int mobTypeID = Integer.parseInt(mobNode.getElementsByTagName("id")
						.item(0).getTextContent());
	
				NodeList pathContainerNodes = mobNode.getElementsByTagName("path");
	
				Vector2i path[] = null;
	
				if (pathContainerNodes.getLength() > 0) {
					Element pathContainer = (Element) pathContainerNodes.item(0);
	
					NodeList pathNodes = pathContainer.getElementsByTagName("node");
	
					path = new Vector2i[pathNodes.getLength()];
	
					for (int p = 0; p < pathNodes.getLength(); p++) {
						Element pathNode = (Element) pathNodes.item(p);
	
						int pX = Integer.parseInt(pathNode.getAttribute("xPos"));
						int pY = Integer.parseInt(pathNode.getAttribute("yPos"));
						path[p] = new Vector2i(pX, pY);
					}
				}
				Mob mob = new Mob(gc, game, mobTypeID, this, path);
				entityManager.addMob(mob);
			}
		}
			
		fogOfWarTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0, 0,
				Game.TILE_SIZE, Game.TILE_SIZE);
		mapBorderTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0, 32,
				Game.TILE_SIZE, Game.TILE_SIZE);
		fogOfWar = new boolean[getWidth()][getHeight()];
		for (boolean row[] : fogOfWar)
			Arrays.fill(row, true);
		collisionMap = new CollisionMap(this);
		nodeMatrix = AStar.getNodeMatrix(collisionMap);
		tsGUI = new TileSelectorGUI(gc, game);
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
	
	public boolean isOccupied(int tX, int tY) {
		return layers[layers.length - 1].getOccupied(tX, tY);
	}
	
	public MapTileData setOccupied(int x, int y, MovingEntity entity) {
		return layers[layers.length - 1].setOccupied(x, y, entity);
	}

	public void revealCoordinate(int tX, int tY) {
		fogOfWar[tX][tY] = false;
	}

	public Vector2i getOffset() {
		return offset;
	}

	public void setTile(int tX, int tY, int tileID, int layer) {
		layers[layer].setTile(tX, tY, tileID);
	}

	public void focusTile(Vector2i tile) {
		// Take tile position, convert to screen position, and set it as offset
		setOffset(new Vector2i(
				-(tile.getX() * Game.TILE_SIZE - game.WIDTH / 2), (tile.getY()
						* Game.TILE_SIZE - game.HEIGHT / 2)));
	}

	public void setOffset(Vector2i offset) {
		if (!getEditing()) {
			if (offset.getX() > 150)
				offset.setX(150);
			if (offset.getY() > 150)
				offset.setY(150);

			if (width * Game.TILE_SIZE + offset.getX() < game.WIDTH - 150)
				offset.setX(game.WIDTH - 150 - width * Game.TILE_SIZE);

			if (height * Game.TILE_SIZE + offset.getY() < game.HEIGHT - 150)
				offset.setY(game.HEIGHT - 150 - height * Game.TILE_SIZE);
		}
		this.offset = offset;
	}

	public Vector2i getSuitablePlayerLocation(WorldMap.MapDirection direction) {

		// General algorithm
		// Record positions for valid positions
		// Take n/2nd position
		ArrayList<Vector2i> validPositions = new ArrayList<Vector2i>();
		switch (direction) {
			case NORTH:
				System.out.println("Calculating north positions");
				for (int x = 1; x < width - 1; x++)
					if (!getCollideable(x, 0) && !getCollideable(x, 1))
						validPositions.add(new Vector2i(x, 1));
				break;
			case EAST:
				System.out.println("Calculating east positions");
				for (int y = 1; y < height - 1; y++)
					if (!getCollideable(width - 1, y)
							&& !getCollideable(width - 2, y))
						validPositions.add(new Vector2i(width - 2, y));
				break;
			case SOUTH:
				System.out.println("Calculating south positions");
				for (int x = 1; x < width - 1; x++)
					if (!getCollideable(x, height - 1)
							&& !getCollideable(x, height - 2))
						validPositions.add(new Vector2i(x, height - 2));
				break;
			case WEST:
				System.out.println("Calculating west positions");
				for (int y = 1; y < height - 1; y++)
					if (!getCollideable(0, y) && !getCollideable(1, y))
						validPositions.add(new Vector2i(1, y));
				break;
		}
		if (validPositions.size() > 0)
			return validPositions
					.get((int) Math.floor(validPositions.size() / 2));
		return new Vector2i(0, 0);
	}

	public boolean getEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public int getID() {
		return id;
	}

	public void exportXML() {
		try {
			// Export map to XML format
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.newDocument();

			Element rootElement = document.createElement("map");
			document.appendChild(rootElement);
			Element mapRoot = document.createElement("tileData");
			rootElement.appendChild(mapRoot);
			Element mobRoot = document.createElement("mobs");
			rootElement.appendChild(mobRoot);

			for (MapLayer layer : layers) {
				layer.exportXML(document, mapRoot);
			}

			// Export XML to file
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StreamResult result = new StreamResult(new File("mapExported.xml"));
			transformer.transform(new DOMSource(document), result);
			System.out.println("Saved map to mapExported.xml!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
