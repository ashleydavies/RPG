package com.adavieslyons.legacy.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * @author Ashley
 */
public class Colour {
    Color restoreColour;
    Graphics restoreGraphics;
    private Color colour;

    public Colour(String color) {
        // setColour(new Colour());
    }

    public void setTo(Graphics graphics) {
        restoreGraphics = graphics;
        restoreColour = graphics.getColor();
        graphics.setColor(getColour());
    }

    public void restore() {
        restoreGraphics.setColor(restoreColour);
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }
}
