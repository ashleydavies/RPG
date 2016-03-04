package uk.daviesl.rpg.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.gui.TileSelectorGUI;
import uk.daviesl.rpg.util.Vector2i;

/**
 * Created by Ashley on 03/03/2016.
 */
public class EditableMap extends Map {
    private TileSelectorGUI tsGUI;
    private int tileEditingTile = 0;
    private boolean tsGUIOpen;

    private EditableMap(GameContainer gc, GameState game, int width, int height, MapLayer[] layers) {
        super(gc, game, width, height, layers);

        this.tsGUI = new TileSelectorGUI(gc, game);
    }

    public EditableMap createNew(GameContainer gc, GameState game, int width, int height) {
        MapLayer[] layers = new MapLayer[1];
        MapTileData[][] mapTileData = new MapTileData[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapTileData[y][x] = new MapTileData(10);
            }
        }

        layers[0] = new MapLayer(mapTileData);

        return new EditableMap(gc, game, width, height, layers);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        super.render(gc, graphics);

        for (MapLayer layer : layers)
            layer.render(gc, graphics, this, getTotalDelta(), false);

        if (tsGUIOpen) {
            tsGUI.render(gc, graphics);
        }
    }

    @Override
    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        super.update(gc, game, delta);


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
                    && mouseTile.getX() < getWidth()
                    && mouseTile.getY() < getHeight()) {
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

}
