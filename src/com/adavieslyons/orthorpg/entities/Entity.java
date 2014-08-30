package com.adavieslyons.orthorpg.entities;

import org.newdawn.slick.*;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

/**
 * 
 * @author Ashley
 */
public abstract class Entity {
	public abstract void update(GameContainer gc, GameState game, int delta) throws SlickException;
	
	public abstract void render(GameContainer gc, Graphics graphics) throws SlickException;
}
