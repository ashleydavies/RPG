package uk.daviesl.rpg.util.map;

import uk.daviesl.rpg.Game;
import uk.daviesl.rpg.entities.EntityManager;
import uk.daviesl.rpg.entities.MovingEntity;
import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.gui.ImageGUI;
import uk.daviesl.rpg.util.SpriteSheet;
import uk.daviesl.rpg.util.Vector2i;
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

/**
 * @author Ashley
 */
public abstract class Map {
    private final int width;
    private final int height;
    private Image borderTexture;
    private Image isoDistinguishTexture;
    private int totalDelta;
    protected MapLayer layers[];

    private Vector2i offset = new Vector2i(0, 0);

    protected Map(GameContainer gc, GameState game, int width, int height, MapLayer[] layers) {
        this.width = width;
        this.height = height;
        this.layers = layers;

        this.borderTexture = coreTextureImage(0, 4, 1, 1);
        this.isoDistinguishTexture = coreTextureImage(0, 5, 1, 1);
        mapSetup(gc, game);
    }

    public void update(GameContainer gc, GameState game, int delta)
            throws SlickException {

        totalDelta += delta;


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

    public void render(GameContainer gc, Graphics graphics) throws SlickException {
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
    protected Vector2i screenCoordinatesToTileCoordinates(Vector2i coordinates) {
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

    private void mapSetup(GameContainer gc, GameState game) {
        generateMinimapImage();
        minimapBackgroundGUI = new ImageGUI(gc, game, 16, 16, minimapImage);
    }

    protected Image coreTextureImage(int tiles_x, int tiles_y, int tiles_w, int tiles_h) {
        return SpriteSheet.getSubImage(0, (Game.TILE_SIZE_X + 1) * tiles_x, (Game.TILE_SIZE_Y + 1) * tiles_y,
                (Game.TILE_SIZE_X + 1) * tiles_w, (Game.TILE_SIZE_Y + 1) * tiles_h);
    }



    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    protected void setTile(int tX, int tY, int tileID, int layer) {
        layers[layer].setTile(tX, tY, tileID);
    }

    protected MapTile getTile(int tX, int tY, int layer) {
        return layers[layer].getTile(tX, tY);
    }

    protected boolean isTileAtPosition(int tX, int tY) {
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

    protected int getTotalDelta() {
        return totalDelta;
    }

    public Image getBorderTexture() {
        return borderTexture;
    }
}
