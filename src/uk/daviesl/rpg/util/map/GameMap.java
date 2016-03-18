package uk.daviesl.rpg.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import uk.daviesl.rpg.Game;
import uk.daviesl.rpg.entities.EntityManager;
import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.gui.ImageGUI;
import uk.daviesl.rpg.util.FileLoader;
import uk.daviesl.rpg.util.Vector2i;
import uk.daviesl.rpg.util.map.pathfinding.AStar;
import uk.daviesl.rpg.util.map.pathfinding.CollisionMap;
import uk.daviesl.rpg.util.xml.MapData;
import uk.daviesl.rpg.util.xml.RPGXML;

import java.util.Arrays;

/**
 * Created by Ashley on 29/02/2016.
 */
public class GameMap extends Map {
    private int mapID;
    private GameState game;
    private EntityManager entityManager;
    private boolean fogOfWar[][];
    private boolean occupiedTiles[][];
    private CollisionMap collisionMap;
    private AStar.Node[][] nodeMatrix;
    private Image minimapImage;
    private ImageGUI minimapBackgroundGUI;
    private Image fogOfWarTexture;
    private Image fogOfWarRevealedTexture;

    protected GameMap(GameContainer gc, GameState game, int mapID, int width, int height, MapLayer[] layers, EntityManager entityManager) {
        super(gc, game, width, height, layers);

        this.mapID = mapID;
        this.game = game;
        this.entityManager = entityManager;

        this.collisionMap = new CollisionMap(this);
        this.nodeMatrix = AStar.getNodeMatrix(collisionMap);

        this.fogOfWar = new boolean[getWidth()][getHeight()];
        this.fogOfWarTexture = coreTextureImage(0, 0, 1, 1);
        this.fogOfWarRevealedTexture = coreTextureImage(0, 4, 1, 1);

        for (boolean row[] : fogOfWar)
            Arrays.fill(row, true);
    }

    public static GameMap loadMap(GameContainer gc, GameState game, int mapID) {
        EntityManager entityManager = new EntityManager(game);
        MapData data = RPGXML.loadMap(game, FileLoader.getXML("map/" + mapID), entityManager);
        data.getMobs().forEach(entityManager::addMob);

        return new GameMap(gc, game, mapID, data.getWidth(), data.getHeight(), data.getLayers(), entityManager);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        super.render(gc, graphics);
    }

    public boolean areEntitiesVisible(Vector2i position) {
        return (position.distance(entityManager.getPlayer().getPosition()) <= entityManager
                .getPlayer().getFieldOfView());

    }

    public void renderPostEntities(GameContainer gc, Graphics graphics) {
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

        minimapBackgroundGUI.render(gc, graphics);
    }

    public AStar.Node[][] getNodeMatrix() {
        return nodeMatrix;
    }

    public boolean isFogOfWar(int tX, int tY) {
        return fogOfWar[tX][tY];
    }

    public void revealCoordinate(int tX, int tY) {
        fogOfWar[tX][tY] = false;
    }

    private void mapSetup(GameContainer gc, GameState game) {
        generateMinimapImage();
        minimapBackgroundGUI = new ImageGUI(gc, game, 16, 16, minimapImage);
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

    public int getMapID() {
        return mapID;
    }

    public GameState getGame() {
        return game;
    }

    @Override
    protected void setOffset(Vector2i offset) {
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

        super.setOffset(offset);
    }
}
