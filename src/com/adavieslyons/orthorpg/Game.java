package com.adavieslyons.orthorpg;

import org.newdawn.slick.*;

import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.states.GameState;

/**
 * 
 * @author Ashley
 */
public class Game extends BasicGame {
	public static final int TILE_SIZE = 32;

	GameStateManager gsm;

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Game());
		app.setDisplayMode(1200, 800, false);
		app.setShowFPS(false);
		// app.setTargetFrameRate(100);
		app.start();
	}

	private Game() {
		super("RPG");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gsm = new GameStateManager();
		GameState s = new GameState(gsm);
		gsm.pushState(gc, s);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		gsm.update(gc, delta);
	}

	@Override
	public void render(GameContainer gc, Graphics graphics)
			throws SlickException {
		gsm.render(gc, graphics);
	}
}
