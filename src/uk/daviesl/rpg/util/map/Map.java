package uk.daviesl.rpg.util.map;

import uk.daviesl.rpg.Game;
import uk.daviesl.rpg.entities.EntityManager;
import uk.daviesl.rpg.entities.MovingEntity;
import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.gui.ImageGUI;
import uk.daviesl.rpg.gui.TileSelectorGUI;
import uk.daviesl.rpg.util.FileLoader;
import uk.daviesl.rpg.util.map.pathfinding.CollisionMap;
import uk.daviesl.rpg.util.SpriteSheet;
import uk.daviesl.rpg.util.Vector2i;
import uk.daviesl.rpg.util.map.pathfinding.AStar;
import uk.daviesl.rpg.util.map.pathfinding.AStar.Node;
import uk.daviesl.rpg.util.xml.MapData;
import uk.daviesl.rpg.util.xml.RPGXML;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private int id;
    private EntityManager entityManager;
    private GameState game;
    private int width = -1;
    private int height;
    private MapLayer layers[];
    private boolean fogOfWar[][];
    private boolean occupiedTiles[][];
    private CollisionMap collisionMap;
    private Node[][] nodeMatrix;
    private Image fogOfWarTexture;
    private Image fogOfWarRevealedTexture;
    private Image borderTexture;
    private Image isoDistinguishTexture;
    private Image minimapImage;
    private TileSelectorGUI tsGUI;
    private ImageGUI minimapBackgroundGUI;
    private int tileEditingTile = 0;
    private boolean tsGUIOpen;
    private boolean editing;
    private int totalDelta;

    private Vector2i offset = new Vector2i(0, 0);

    public Map(GameContainer gc, GameState game, int mapID, EntityManager entityManager) {
        MapData data = RPGXML.loadMap(game, this, FileLoader.getXML("map/" + mapID));

        this.game = game;
        this.id = mapID;
        this.entityManager = entityManager;
        this.height = data.getHeight();
        this.width = data.getWidth();
        this.layers = data.getLayers();
        this.collisionMap = new CollisionMap(this);
        this.nodeMatrix = AStar.getNodeMatrix(collisionMap);
        this.tsGUI = new TileSelectorGUI(gc, game);
        this.fogOfWar = new boolean[getWidth()][getHeight()];
        this.fogOfWarTexture = coreTextureImage(0, 0, 1, 1);
        this.fogOfWarRevealedTexture = coreTextureImage(0, 4, 1, 1);
        this.borderTexture = coreTextureImage(0, 4, 1, 1);
        this.isoDistinguishTexture = coreTextureImage(0, 5, 1, 1);

        data.getMobs().forEach(entityManager::addMob);

        for (boolean row[] : fogOfWar)
            Arrays.fill(row, true);

        mapSetup(gc, game);
    }

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
                tsGUI.update(gc, delta);
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
            addOffset(delta, 0);
        if (game.getInput().isKeyDown(Input.KEY_RIGHT))
            addOffset(-1 * delta, 0);

        if (game.getInput().isKeyDown(Input.KEY_UP))
            addOffset(0, delta);
        if (game.getInput().isKeyDown(Input.KEY_DOWN))
            addOffset(0, -1 * delta);
    }

    private void fillMap(int x, int y, int tileID, int newTileID, int layer) {
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

    private void addOffset(int x, int y) {
        addOffset(new Vector2i(x, y));
    }

    private void addOffset(Vector2i offset) {
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

    public void renderPostEntities(GameContainer gc, Graphics graphics) {
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
    private Vector2i screenCoordinatesToTileCoordinates(Vector2i coordinates) {
        return screenCoordinatesToTileCoordinates(coordinates.getX(),
                coordinates.getY());
    }

    // TILE => GAME
    private Vector2i tileCoordinatesToGameCoordinates(int x, int y) {
        return new Vector2i((int) ((x * 0.5 - y * 0.5) * Game.TILE_SIZE_X),
                (int) ((y * 0.5 + x * 0.5) * Game.TILE_SIZE_Y));
    }

    public Vector2i tileCoordinatesToScreenCoordinates(int x, int y) {
        return new Vector2i((int) ((x * 0.5 - y * 0.5) * Game.TILE_SIZE_X),
                (int) ((y * 0.5 + x * 0.5) * Game.TILE_SIZE_Y)).add(offset);
    }

    // TILE => GAME
    public Vector2i tileCoordinatesToGameCoordinates(Vector2i tileCoordinates) {
        return tileCoordinatesToGameCoordinates(tileCoordinates.getX(),
                tileCoordinates.getY());
    }

    // SCREEN => GAME
    private Vector2i screenCoordinatesToGameCoordinates(
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

    private void generateNewMap(GameContainer gc, GameState game, int mapID,
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
        fogOfWar = new boolean[getWidth()][getHeight()];
        for (boolean row[] : fogOfWar)
            Arrays.fill(row, true);
        collisionMap = new CollisionMap(this);
        nodeMatrix = AStar.getNodeMatrix(collisionMap);
        tsGUI = new TileSelectorGUI(gc, game);
        // If we generated a new map, chances are we want to edit it.
        this.setEditing(true);

        mapSetup(gc, game);
    }

    private void mapSetup(GameContainer gc, GameState game) {
        generateMinimapImage();
        minimapBackgroundGUI = new ImageGUI(gc, game, 16, 16, minimapImage);
    }

    private Image coreTextureImage(int tiles_x, int tiles_y, int tiles_w, int tiles_h) {
        return SpriteSheet.getSubImage(0, (Game.TILE_SIZE_X + 1) * tiles_x, (Game.TILE_SIZE_Y + 1) * tiles_y,
                (Game.TILE_SIZE_X + 1) * tiles_w, (Game.TILE_SIZE_Y + 1) * tiles_h);
    }

    private void generateMinimapImage() {
        try {
            this.minimapImage = new Image(this.getWidth(), this.getHeight());
            Graphics graphics = minimapImage.getGraphics();
            for (int x = 0; x < this.getWidth(); x++) {
                for (int y = 0; y < this.getHeight(); y++) {
                    graphics.setColor(layers[0].getTile(x, y).getMinimapColor());
                    graphics.drawRect(x, y, 1, 1);
                }
            }
            graphics.flush();
        } catch (SlickException e) {
            System.out.println("Unrecoverable failure to initialise or draw to image canvas for outlined image.");
        }
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

    private void setOffset(Vector2i offset) {
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

    private void setTile(int tX, int tY, int tileID, int layer) {
        layers[layer].setTile(tX, tY, tileID);
    }

    private MapTile getTile(int tX, int tY, int layer) {
        return layers[layer].getTile(tX, tY);
    }

    private boolean isTileAtPosition(int tX, int tY) {
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
        ArrayList<Vector2i> validPositions = new ArrayList<>();
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

    public Image getBorderTexture() {
        return borderTexture;
    }
}
