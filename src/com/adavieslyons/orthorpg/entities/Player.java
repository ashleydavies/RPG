package com.adavieslyons.orthorpg.entities;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.InventoryGUI;

/**
 * 
 * @author Ashley
 */
public class Player extends Entity {
	Point position;
	Image image;
	InventoryGUI inventoryGUI;
	
	public Player(GameContainer gc, GameState game) throws SlickException {
		position = new Point(0, 0);
		image = new Image("img/player.png");
		inventoryGUI = new InventoryGUI(gc, game);
	}
	
	@Override
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		if (Keyboard.isKeyDown(Input.KEY_UP)) {
			position.setY(position.getY() - 0.1f * delta);
		} else if (Keyboard.isKeyDown(Input.KEY_DOWN)) {
			position.setY(position.getY() + 0.1f * delta);
		}
		
		if (Keyboard.isKeyDown(Input.KEY_LEFT)) {
			position.setX(position.getX() - 0.1f * delta);
		} else if (Keyboard.isKeyDown(Input.KEY_RIGHT)) {
			position.setX(position.getX() + 0.1f * delta);
		}
		
		inventoryGUI.update(gc, game, delta);
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		image.draw((int) position.getX(), (int) position.getY());
		inventoryGUI.render(gc, graphics);
	}
}