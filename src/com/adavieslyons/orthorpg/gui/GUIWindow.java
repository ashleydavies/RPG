package com.adavieslyons.orthorpg.gui;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public abstract class GUIWindow {
    static final int BW = 12; // Border Width

    static Image ui;

    Rectangle windowRect;
    Image windowBg;
    Image windowDefaultContent;
    Image windowDynamicContent;
    GameState game;

    static {
        try {
            ui = new Image("img/ui/ui.png");
        } catch (SlickException e) {
        }
    }

    public GUIWindow(GameContainer gc, GameState game, int width, int height)
            throws SlickException {
        this.game = game;

        int x = gc.getWidth() / 2 - width / 2;
        int y = gc.getHeight() / 2 - height / 2;

        windowRect = new Rectangle(x, y, width, height);

        windowBg = new Image(width, height);
        windowDefaultContent = new Image(width, height);
        windowDynamicContent = new Image(width, height);

        renderWindow(gc);
        renderDefaultContent(gc);
    }

    public void renderWindow(GameContainer gc) {
        Image border_tl = ui.getSubImage(0, 0, BW, BW);
        Image border_tr = ui.getSubImage(BW, 0, BW, BW);
        Image border_bl = ui.getSubImage(0, BW, BW, BW);
        Image border_br = ui.getSubImage(BW, BW, BW, BW);
        Image border_t = ui.getSubImage(BW, BW * 2, BW, BW);
        Image border_l = ui.getSubImage(0, BW * 2, BW, BW);
        Image border_r = ui.getSubImage(BW, BW * 3, BW, BW);
        Image border_b = ui.getSubImage(0, BW * 3, BW, BW);
        Image inner = ui.getSubImage(BW * 2, 0, BW * 5, BW * 5);

        Graphics graphics = gc.getGraphics();

        graphics.clear();

        graphics.drawImage(border_tl, 0, 0);
        graphics.drawImage(border_tr, windowRect.getWidth() - BW, 0);
        graphics.drawImage(border_bl, 0, windowRect.getHeight() - BW);
        graphics.drawImage(border_br, windowRect.getWidth() - BW,
                windowRect.getHeight() - BW);

        graphics.fillRect(BW, 0, windowRect.getWidth() - BW * 2, BW, border_t,
                0, 0);

        graphics.fillRect(BW, windowRect.getHeight() - BW,
                windowRect.getWidth() - BW * 2, BW, border_b, 0, 0);

        graphics.fillRect(0, BW, BW, windowRect.getHeight() - BW * 2, border_l,
                0, 0);

        graphics.fillRect(windowRect.getWidth() - BW, BW, BW,
                windowRect.getHeight() - BW * 2, border_r, 0, 0);

        graphics.fillRect(BW, BW, windowRect.getWidth() - BW * 2,
                windowRect.getHeight() - BW * 2, inner, 0, 0);

        graphics.copyArea(windowBg, 0, 0);
        graphics.clear();
    }

    public void renderDefaultContent(GameContainer gc) {
        Graphics graphics = gc.getGraphics();
        graphics.clear();

        // Draw standard menu UI
        graphics.setColor(Color.black);
        graphics.drawString(game.getCurrentGameData().getIntSaveData(0)
                + " coins", 24, windowRect.getHeight() - 36);

        graphics.copyArea(windowDefaultContent, 0, 0);
        graphics.clear();
    }

    public abstract void render(GameContainer gc, Graphics graphics)
            throws SlickException;

    public abstract void update(GameContainer gc, int delta)
            throws SlickException;
    // public abstract void renderPrimaryContent(GameContainer gc);
}
