package com.adavieslyons.legacy.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * @author Ashley
 */
public class Button extends GUIElement {
    Colour background;

    public Button(Rectangle a_rect) {
        super(a_rect);
        background = new Colour("green");
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        background.setTo(graphics);
        graphics.fillRect(rect.getX(), rect.getY(), rect.getWidth(),
                rect.getHeight());
        background.restore();
    }
}
