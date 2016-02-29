package uk.daviesl.rpg.gui;

import uk.daviesl.rpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

// Minimal implementation of GUIWindow, just for rendering a raw image in a window
public class ImageGUI extends GUIWindow {
    private int x;
    private int y;
    private Image image;

    public ImageGUI(GameContainer gc, GameState game, int x, int y, Image image) {
        super(gc, game, image.getWidth() + BW * 2, image.getHeight() + BW * 2);
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        graphics.drawImage(windowBg, x, y);
        graphics.drawImage(image, x + BW, y + BW);
    }

    @Override
    public void update(GameContainer gc, int delta) {

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
