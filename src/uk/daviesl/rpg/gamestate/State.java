package uk.daviesl.rpg.gamestate;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public abstract class State {
    protected final GameStateManager gameStateManager;

    public State(GameStateManager gsm) {
        gameStateManager = gsm;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public abstract void load(GameContainer gc) throws SlickException;

    public abstract void unload();

    public abstract void update(GameContainer gc, int delta)
            throws SlickException;

    public abstract void render(GameContainer gc, Graphics graphics)
            throws SlickException;

    public abstract void mouseClicked(int button, int x, int y, int clickCount);

    public abstract void keyPressed(int key, char c);
}
