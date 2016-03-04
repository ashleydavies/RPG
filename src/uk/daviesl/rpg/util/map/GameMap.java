package uk.daviesl.rpg.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
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
        MapData data = RPGXML.loadMap(game, FileLoader.getXML("map/" + mapID));
        EntityManager entityManager = new EntityManager(game);
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

}
