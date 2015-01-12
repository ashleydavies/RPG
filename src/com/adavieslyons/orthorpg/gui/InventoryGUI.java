package com.adavieslyons.orthorpg.gui;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.inventory.ItemStack;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public class InventoryGUI extends GUIWindow {
    GameState game;

    public InventoryGUI(GameContainer gc, GameState game) throws SlickException {
        super(gc, game, 504, 624);
        this.game = game;

        renderPrimaryContent(gc);
    }

    public void renderPrimaryContent(GameContainer gc) throws SlickException {
        Image itemSlot = ui.getSubImage(84, 0, 48, 48);

        Graphics graphics = gc.getGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));
        graphics.clear();

        // Width of the core inventory slots
        int totalWidth = 48 * 9 + 9;

        // Draw main inventory slots; 9x3 grid
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                graphics.drawImage(itemSlot, windowRect.getWidth() / 2
                        - totalWidth / 2 + 49 * x, windowRect.getHeight()
                        - (y + 1) * 49 - BW - 18);

        // Draw ground slots
        for (int y = 0; y < 6; y++)
            for (int x = 0; x < 2; x++)
                graphics.drawImage(itemSlot, windowRect.getWidth() - BW * 2
                        - 18 - 49 * x - 38, BW + 18 + y * 49);

        graphics.drawImage(game.getPlayer().getImage(), 130, 80, 260, 300, 0,
                0, game.getPlayer().getImage().getWidth(), game.getPlayer()
                        .getImage().getHeight(), Color.white);
        graphics.copyArea(windowDynamicContent, 0, 0);
        graphics.clear();
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        graphics.drawImage(windowBg, windowRect.getX(), windowRect.getY());
        graphics.drawImage(windowDynamicContent, windowRect.getX(),
                windowRect.getY());

        // Render player items (this'll be a PITA)
        float itemOriginX = windowRect.getX() + windowRect.getWidth() / 2
                - (48 * 9 + 9) / 2 + 8;
        float itemOriginY = windowRect.getY() + windowRect.getHeight() - 147
                - BW - 18 + 8;

        ItemStack items[] = game.getPlayer().getItems();
        int iX = 0;
        int iY = 0;
        for (ItemStack itemStack : items) {
            if (itemStack != null) {
                float rX = itemOriginX + iX * 49;
                float rY = itemOriginY + iY * 49;

                Image itemImage = itemStack.getItem().getImage();
                graphics.drawImage(itemImage, rX, rY);
            }

            iX++;
            if (iX == 9) {
                iX = 0;
                iY++;
            }
        }
    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {

    }
}
