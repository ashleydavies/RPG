package com.adavieslyons.util.map;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.entities.EntityManager;
import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.orthorpg.entities.MovingEntity;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.ImageGUI;
import com.adavieslyons.orthorpg.gui.TileSelectorGUI;
import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.XMLParser;
import com.adavieslyons.util.map.pathfinding.AStar;
import com.adavieslyons.util.map.pathfinding.AStar.Node;
import com.adavieslyons.util.map.pathfinding.CollisionMap;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
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
    Image fogOfWarRevealedTexture;
    Image mapBorderTexture;
    Image isoDistinguishTexture;
    Image minimapImage;
    TileSelectorGUI tsGUI;
    ImageGUI minimapBackgroundGUI;
    int tileEditingTile = 0;
    boolean tsGUIOpen;
    boolean editing;
    int totalDelta;

    Vector2i offset = new Vector2i(0, 0);

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
                        && mouseTile.getY() < height) {
                    if (game.getInput().isKeyDown(Input.KEY_F)) {
                        // FILL
                        int tileID = getTile(mouseTile.getX(), mouseTile.getY(), 0).id;
                        if (tileID == tileEditingTile)
                            return;
                        fillMap(mouseTile.getX(), mouseTile.getY(), tileID, tileEditingTile, 0);
                    } else {
                        setTile(mouseTile.getX(), mouseTile.getY(),
                                tileEditingTile, 0);
                    }
                }
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

    public void fillMap(int x, int y, int tileID, int newTileID, int layer) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
            return;
        if (layers[layer].getTile(x, y).id != tileID)
            return;

        setTile(x, y, newTileID, layer);

        fillMap(x + 1, y, tileID, newTileID, layer);
        fillMap(x - 1, y, tileID, newTileID, layer);
        fillMap(x, y + 1, tileID, newTileID, layer);
        fillMap(x, y - 1, tileID, newTileID, layer);

        fillMap(x - 1, y - 1, tileID, newTileID, layer);
        fillMap(x + 1, y - 1, tileID, newTileID, layer);
        fillMap(x - 1, y + 1, tileID, newTileID, layer);
        fillMap(x + 1, y + 1, tileID, newTileID, layer);
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
            layer.render(gc, graphics, totalDelta, !this.getEditing());
        // Only render mobs if not editing
        if (getEditing() && tsGUIOpen)
            tsGUI.render(gc, graphics);
    }

    public void renderPostEntities(GameContainer gc, Graphics graphics)
            throws SlickException {
        if (!editing) {
            Vector2i minXTile = screenCoordinatesToTileCoordinates(0, 0);
            Vector2i minYTile = screenCoordinatesToTileCoordinates(gc.getWidth(), 0);
            Vector2i maxYTile = screenCoordinatesToTileCoordinates(0, gc.getHeight());
            Vector2i maxXTile = screenCoordinatesToTileCoordinates(gc.getWidth(), gc.getHeight());
            if (minXTile.getX() < 0)
                minXTile.setX(0);
            if (minYTile.getY() < 0)
                minYTile.setY(0);
            if (maxXTile.getX() > getWidth())
                maxXTile.setX(getWidth());
            if (maxYTile.getY() > getHeight())
                maxYTile.setY(getHeight());

            for (int y = minYTile.getY(); y < maxYTile.getY(); y++) {
                for (int x = minXTile.getX(); x < maxXTile.getX(); x++) {
                    if (isTileAtPosition(x, y)) {
                        Vector2i center = entityManager.getPlayer().getPosition();
                        Vector2i renderPosition = tileCoordinatesToScreenCoordinates(
                                x, y);
                        if (center.distance(new Vector2i(x, y)) <= entityManager
                                .getPlayer().getFieldOfView()) {
                            fogOfWar[x][y] = false;
                        } else if (fogOfWar[x][y]) {
                            fogOfWarTexture.draw(renderPosition.getX(),
                                    renderPosition.getY());
                        } else {
                            fogOfWarRevealedTexture.draw(renderPosition.getX(),
                                    renderPosition.getY());
                        }
                    }
                }
            }
        }

        minimapBackgroundGUI.render(gc, graphics);
    }

    public boolean areEntitiesVisible(Vector2i position) {
        return (position.distance(entityManager.getPlayer().getPosition()) <= entityManager
                .getPlayer().getFieldOfView());

    }

    // SCREEN => TILE
    public Vector2i screenCoordinatesToTileCoordinates(int x, int y) {
        Vector2i noOffset = screenCoordinatesToGameCoordinates(new Vector2i(x, y));

        int tCoordX = noOffset.getX() / Game.TILE_SIZE_X + noOffset.getY()
                / Game.TILE_SIZE_Y;
        int tCoordY = -(noOffset.getX() / Game.TILE_SIZE_X - noOffset.getY()
                / Game.TILE_SIZE_Y);
        // Check if we need to move across depending on image map
        int xRel = noOffset.getX() % Game.TILE_SIZE_X;
        int yRel = noOffset.getY() % Game.TILE_SIZE_Y;

        // if xRel is negative we need to shift (-1, +1)
        if (xRel < 0) {
            tCoordX--;
            tCoordY++;
            xRel = 64 + xRel;
        }
        switch (isoDistinguishTexture.getColor(xRel, yRel).getGreen()) {
            case 0:
                return new Vector2i(tCoordX, tCoordY);
            case 20:
                return new Vector2i(tCoordX - 1, tCoordY);
            case 40:
                return new Vector2i(tCoordX, tCoordY - 1);
            case 60:
                return new Vector2i(tCoordX, tCoordY + 1);
            case 80:
                return new Vector2i(tCoordX + 1, tCoordY);
        }

        return new Vector2i(tCoordX, tCoordY);
    }

    // SCREEN => TILE
    public Vector2i screenCoordinatesToTileCoordinates(Vector2i coordinates) {
        return screenCoordinatesToTileCoordinates(coordinates.getX(),
                coordinates.getY());
    }

    // TILE => GAME
    public Vector2i tileCoordinatesToGameCoordinates(int x, int y) {
        float xP = x;
        float yP = y;

        return new Vector2i((int) ((xP * 0.5 - yP * 0.5) * Game.TILE_SIZE_X),
                (int) ((yP * 0.5 + xP * 0.5) * Game.TILE_SIZE_Y));
    }

    public Vector2i tileCoordinatesToScreenCoordinates(int x, int y) {
        float xP = x;
        float yP = y;

        return new Vector2i((int) ((xP * 0.5 - yP * 0.5) * Game.TILE_SIZE_X),
                (int) ((yP * 0.5 + xP * 0.5) * Game.TILE_SIZE_Y)).add(offset);
    }

    // TILE => GAME
    public Vector2i tileCoordinatesToGameCoordinates(Vector2i tileCoordinates) {
        return tileCoordinatesToGameCoordinates(tileCoordinates.getX(),
                tileCoordinates.getY());
    }

    // SCREEN => GAME
    public Vector2i screenCoordinatesToGameCoordinates(
            Vector2i screenCoordinates) {
        return screenCoordinates.subtract(offset);
    }

    // SCREEN => GAME
    public Vector2i screenCoordinatesToGameCoordinates(Vector2f renderPosition) {
        return screenCoordinatesToGameCoordinates(new Vector2i(
                (int) renderPosition.getX(), (int) renderPosition.getY()));
    }

    // GAME => SCREEN
    public Vector2i gameCoordinatesToScreenCoordinates(Vector2i gameCoordinates) {
        return gameCoordinates.add(offset);
    }

    public void generateNewMap(GameContainer gc, GameState game, int mapID,
                               EntityManager entityManager) throws SlickException {
        generateNewMap(gc, game, mapID, entityManager, 80, 80);
    }

    public void generateNewMap(GameContainer gc, GameState game, int mapID,
                               EntityManager entityManager, int width, int height)
            throws SlickException {
        this.game = game;
        this.id = mapID;
        this.entityManager = entityManager;
        this.width = width;
        this.height = height;

        layers = new MapLayer[1];
        MapTileData[][] mapTileData = new MapTileData[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapTileData[y][x] = new MapTileData(10);
            }
        }

        layers[0] = new MapLayer(mapTileData, this);

        // TODO: Duplicate code across this method & load - refactor to fix
        /*
         * fogOfWarTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0, 0,
		 * Game.TILE_SIZE_X, Game.TILE_SIZE_Y); fogOfWarRevealedTexture =
		 * SpriteSheet.getSpriteSheet(0).getSubImage(0, Game.TILE_SIZE_Y * 4,
		 * Game.TILE_SIZE_X, Game.TILE_SIZE_Y); mapBorderTexture =
		 * SpriteSheet.getSpriteSheet(0).getSubImage(0, Game.TILE_SIZE_Y * 4,
		 * Game.TILE_SIZE_X, Game.TILE_SIZE_Y);
		 */
        fogOfWar = new boolean[getWidth()][getHeight()];
        for (boolean row[] : fogOfWar)
            Arrays.fill(row, true);
        collisionMap = new CollisionMap(this);
        nodeMatrix = AStar.getNodeMatrix(collisionMap);
        tsGUI = new TileSelectorGUI(gc, game);
        // If we generated a new map, chances are we want to edit it.
        this.setEditing(true);

        generateMinimapImage();
        minimapBackgroundGUI = new ImageGUI(gc, game, 16, 16, minimapImage);
    }

    public void load(GameContainer gc, GameState game, int mapID,
                     EntityManager entityManager) throws SlickException {
        this.game = game;
        this.id = mapID;
        this.entityManager = entityManager;
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

                int mobTypeID = Integer.parseInt(mobNode
                        .getAttribute("id"));

                int xPos = Integer.parseInt(mobNode.getAttribute("xPos"));
                int yPos = Integer.parseInt(mobNode.getAttribute("xPos"));

                NodeList pathContainerNodes = mobNode
                        .getElementsByTagName("path");

                Vector2i path[] = null;

                if (pathContainerNodes.getLength() > 0) {
                    Element pathContainer = (Element) pathContainerNodes
                            .item(0);

                    NodeList pathNodes = pathContainer
                            .getElementsByTagName("node");

                    path = new Vector2i[pathNodes.getLength()];

                    for (int p = 0; p < pathNodes.getLength(); p++) {
                        Element pathNode = (Element) pathNodes.item(p);

                        int pX = Integer
                                .parseInt(pathNode.getAttribute("xPos"));
                        int pY = Integer
                                .parseInt(pathNode.getAttribute("yPos"));
                        path[p] = new Vector2i(pX, pY);
                    }
                }
                Mob mob = new Mob(gc, game, mobTypeID, this, path);
                mob.setPosition(new Vector2i(xPos, yPos));
                entityManager.addMob(mob);
            }
        }

        fogOfWarTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0, 0,
                Game.TILE_SIZE_X + 1, Game.TILE_SIZE_Y + 1);
        fogOfWarRevealedTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0,
                (Game.TILE_SIZE_Y + 1) * 4, Game.TILE_SIZE_X + 1,
                Game.TILE_SIZE_Y + 1);
        mapBorderTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0,
                (Game.TILE_SIZE_Y + 1) * 4, Game.TILE_SIZE_X + 1,
                Game.TILE_SIZE_Y + 1);
        isoDistinguishTexture = SpriteSheet.getSpriteSheet(0).getSubImage(0,
                (Game.TILE_SIZE_Y + 1) * 5, Game.TILE_SIZE_X + 1,
                Game.TILE_SIZE_Y + 1);

        fogOfWar = new boolean[getWidth()][getHeight()];
        for (boolean row[] : fogOfWar)
            Arrays.fill(row, true);
        collisionMap = new CollisionMap(this);
        nodeMatrix = AStar.getNodeMatrix(collisionMap);
        tsGUI = new TileSelectorGUI(gc, game);

        generateMinimapImage();
        minimapBackgroundGUI = new ImageGUI(gc, game, 16, 16, minimapImage);
    }

    public void generateMinimapImage() throws SlickException {
        this.minimapImage = new Image(this.getWidth(), this.getHeight());
        Graphics graphics = minimapImage.getGraphics();
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                graphics.setColor(layers[0].getTile(x, y).getMinimapColor());
                graphics.drawRect(x, y, 1, 1);
            }
        }
        graphics.flush();
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

    public GameState getGame() {
        return game;
    }

    public MapTileData setOccupied(int x, int y, MovingEntity entity) {
        return layers[layers.length - 1].setOccupied(x, y, entity);
    }

    public boolean getOccupied(int x, int y) {
        return layers[layers.length - 1].getOccupied(x, y);
    }

    public void revealCoordinate(int tX, int tY) {
        fogOfWar[tX][tY] = false;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public void setOffset(Vector2i offset) {
        if (!getEditing()) {
            // Find corners of map
            int top = offset.getY();
            int bottom = offset.getY() + (int) (0.5 * (getHeight() + getWidth()) * Game.TILE_SIZE_Y);
            int left = offset.getX() - (int) (0.5 * getHeight() * Game.TILE_SIZE_X);
            int right = offset.getX() + (int) (0.5 * getWidth() * Game.TILE_SIZE_X);

            if (top > 100)
                offset.setY(100);
            if (bottom < game.HEIGHT - 150)
                offset.setY(game.HEIGHT - 150 - ((int) (0.5 * (getHeight() + getWidth()) * Game.TILE_SIZE_Y)));

            if (left > 100)
                offset.setX(100 + (int) (0.5 * getHeight() * Game.TILE_SIZE_X));
            if (right < game.WIDTH - 150)
                offset.setX(game.WIDTH - 150 - (int) (0.5 * getWidth() * Game.TILE_SIZE_X));
        }
        this.offset = offset;
    }

    public void setTile(int tX, int tY, int tileID, int layer) {
        layers[layer].setTile(tX, tY, tileID);
    }

    public MapTile getTile(int tX, int tY, int layer) {
        return layers[layer].getTile(tX, tY);
    }

    public boolean isTileAtPosition(int tX, int tY) {
        for (MapLayer layer : layers) {
            if (layer.getTile(tX, tY).id != 0) {
                return true;
            }
        }

        return false;
    }

    public void focusTile(Vector2i tile) {
        // Take tile position, convert to screen position, and set it as offset
        //setOffset(new Vector2i(
        //		-(tile.getX() * Game.TILE_SIZE_X - game.WIDTH / 2),
        //		-(tile.getY() * Game.TILE_SIZE_Y - game.HEIGHT / 2)));
    }

    public Vector2i getSuitablePlayerLocation(WorldMap.MapDirection direction) {

        // General algorithm
        // Record positions for valid positions
        // Take n/2nd position
        ArrayList<Vector2i> validPositions = new ArrayList<Vector2i>();
        switch (direction) {
            case NORTH:
                for (int x = 1; x < width - 1; x++)
                    if (!getCollideable(x, 0) && !getCollideable(x, 1))
                        validPositions.add(new Vector2i(x, 1));
                break;
            case EAST:
                for (int y = 1; y < height - 1; y++)
                    if (!getCollideable(width - 1, y)
                            && !getCollideable(width - 2, y))
                        validPositions.add(new Vector2i(width - 2, y));
                break;
            case SOUTH:
                for (int x = 1; x < width - 1; x++)
                    if (!getCollideable(x, height - 1)
                            && !getCollideable(x, height - 2))
                        validPositions.add(new Vector2i(x, height - 2));
                break;
            case WEST:
                for (int y = 1; y < height - 1; y++)
                    if (!getCollideable(0, y) && !getCollideable(1, y))
                        validPositions.add(new Vector2i(1, y));
                break;
        }
        if (validPositions.size() > 0)
            return validPositions
                    .get((int) Math.floor(validPositions.size() / 2));
        return new Vector2i(0, 3);
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
