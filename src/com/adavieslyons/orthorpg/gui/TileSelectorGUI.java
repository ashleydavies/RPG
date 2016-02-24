package com.adavieslyons.orthorpg.gui;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.MapTile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class TileSelectorGUI extends GUIWindow {
    private int selectedTile;
    private boolean tileSelected;

    public TileSelectorGUI(GameContainer gc, GameState game) throws SlickException {
        super(gc, game, 504, 624);
        renderPrimaryContent(gc);
    }

    public void reset() {
        selectedTile = -1;
        tileSelected = false;
    }

    private void renderPrimaryContent(GameContainer gc) {
        Graphics graphics = gc.getGraphics();
        graphics.clear();

        int x = 0;
        for (MapTile tile : MapTile.getTiles()) {
            graphics.drawImage(tile.getBasicTexture(), BW + 8 + x, BW + 8);
            x += Game.TILE_SIZE_X;
        }

        graphics.copyArea(windowDynamicContent, 0, 0);
        graphics.clear();
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        graphics.drawImage(windowBg, windowRect.getX(), windowRect.getY());
        graphics.drawImage(windowDynamicContent, windowRect.getX(),
                windowRect.getY());
    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {
        if (game.getInput().isMouseButtonDown(0)) {
            Vector2i mouseCoords = new Vector2i(game.getInput().getMouseX(), game.getInput().getMouseY());

            if (mouseCoords.getY() > windowRect.getY() + BW + 8 && mouseCoords.getY() < windowRect.getY() + BW + 8 + 32) {
                if (mouseCoords.getX() > windowRect.getX() + BW + 8 && mouseCoords.getX() < windowRect.getX() + BW + 8 + Game.TILE_SIZE_X * MapTile.getTiles().length) {
                    // Inside a tile
                    selectedTile = (int) Math.floor(mouseCoords.getX() - windowRect.getX() - BW - 8) / Game.TILE_SIZE_X;
                    tileSelected = true;
                }
            }
        }
    }

    public boolean getTileSelected() {
        return tileSelected;
    }

    public int getSelectedTile() {
        return selectedTile;
    }
}
