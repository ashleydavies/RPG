package com.sadwhalestudios.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Ashley
 */
public class Colour {
    private Color colour;
    Color restoreColour;
    Graphics restoreGraphics;
    
    public Colour(String color)
    {
        //setColour(new Colour());
    }
    
    public void setTo(Graphics graphics)
    {
        restoreGraphics = graphics;
        restoreColour = graphics.getColor();
        graphics.setColor(getColour());
    }
    
    public void restore()
    {
        restoreGraphics.setColor(restoreColour);
    }
    
    public Color getColour() {
        return colour;
    }
    
    public void setColour(Color colour) {
        this.colour = colour;
    }
}
