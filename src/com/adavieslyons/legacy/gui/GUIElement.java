package com.adavieslyons.legacy.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.*;

/**
 * 
 * @author Ashley
 */
public abstract class GUIElement {
	Rectangle rect;
	
	GUIElement(Rectangle a_rect) {
		rect = a_rect;
	}
	
	public abstract void render(GameContainer gc, Graphics graphics);
	
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	
	public Rectangle getRect() {
		return rect;
	}
}