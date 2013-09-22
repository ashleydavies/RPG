package com.adavieslyons.orthorpg.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

public class InventoryGUI extends GUIWindow {
	public InventoryGUI(GameContainer gc, GameState game) throws SlickException {
		super(gc, game, 504, 504);
		
		renderPrimaryContent(gc);
	}
	
	public void renderPrimaryContent(GameContainer gc) throws SlickException {
		Image itemSlot = ui.getSubImage(84, 0, 48, 48);
		
		Graphics graphics = gc.getGraphics();
		graphics.clear();
		
		// Width of the core inventory slots
		int totalWidth = 48 * 9 + 9;
		
		// Draw main inventory slots; 9x3 grid
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				graphics.drawImage(itemSlot, windowRect.getWidth() / 2 - totalWidth / 2 + 49 * x, windowRect.getHeight() - (y + 1) * 49 - BW - 18);
		
		graphics.copyArea(windowDynamicContent, 0, 0);
		graphics.clear();
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		graphics.drawImage(windowBg, windowRect.getX(), windowRect.getY());
		graphics.drawImage(windowDynamicContent, windowRect.getX(), windowRect.getY());
		
		// TODO: Change default content rendering to allow some sort of flexibility, i.e. enum with values like TextBased or NoContent
		//graphics.drawImage(windowDefaultContent, windowRect.getX(), windowRect.getY()); We don't need the default content in this one
	}
	
	@Override
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		
	}
}