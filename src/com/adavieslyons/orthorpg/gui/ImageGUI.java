package com.adavieslyons.orthorpg.gui;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

// Minimal implementation of GUIWindow, just for rendering a raw image in a window
public class ImageGUI extends GUIWindow {
    int x;
    int y;
    Image image;

    public ImageGUI(GameContainer gc, GameState game, int x, int y, Image image)
            throws SlickException {
        super(gc, game, image.getWidth() + BW * 2, image.getHeight() + BW * 2);
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        graphics.drawImage(windowBg, x, y);
        graphics.drawImage(image, x + BW, y + BW);
    }

    @Override
    public void update(GameContainer gc, GameState game, int delta)
            throws SlickException {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
