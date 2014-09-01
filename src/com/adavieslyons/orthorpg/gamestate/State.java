package com.adavieslyons.orthorpg.gamestate;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public abstract class State {
	@SuppressWarnings("unused")
	private GameStateManager gameStateManager;

	public State(GameStateManager gsm) {
		gameStateManager = gsm;
	}

	public abstract void load(GameContainer gc) throws SlickException;

	public abstract void unload();

	public abstract void update(GameContainer gc, int delta)
			throws SlickException;

	public abstract void render(GameContainer gc, Graphics graphics)
			throws SlickException;
}
