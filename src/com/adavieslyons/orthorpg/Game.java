package com.adavieslyons.orthorpg;

import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.states.MenuState;
import org.newdawn.slick.*;

/**
 * @author Ashley
 */
public class Game extends BasicGame {
    public static final int TILE_SIZE_X = 64;
    public static final int TILE_SIZE_Y = 32;

    GameStateManager gsm;

    private Game() {
        super("RPG");
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(1200, 800, false);
        app.setShowFPS(false);
        // app.setTargetFrameRate(100);
        app.start();
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gsm = new GameStateManager();
        //GameState s = new GameState(gsm);
        MenuState s = new MenuState(gsm);
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
