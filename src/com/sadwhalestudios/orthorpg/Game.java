package com.sadwhalestudios.orthorpg;

import org.newdawn.slick.*;

import com.sadwhalestudios.orthorpg.gamestate.GameStateManager;
import com.sadwhalestudios.orthorpg.gamestate.states.GameState;

/**
 *
 * @author Ashley
 */
public class Game extends BasicGame {
	GameStateManager gsm;
	
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(app.getScreenWidth(), app.getScreenHeight(), true);
        app.start();
    }
    
    private Game() {
        super("Slabrek RPG Test");
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
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        gsm.render(gc, graphics);
    }
}
