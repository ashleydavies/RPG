package uk.daviesl.rpg.gamestate;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    private final List<State> gameStates;
    private State nextState;

    public GameStateManager() {
        gameStates = new ArrayList<>();
    }

    public void setState(GameContainer gc, State state) throws SlickException {
        for (State i_state : gameStates) {
            i_state.unload();
        }

        gameStates.clear();
        pushState(gc, state);
    }

    public void pushState(GameContainer gc, State state) throws SlickException {
        state.load(gc);
        gameStates.add(state);
    }

    public void popState() {
        getActiveState().unload();
        gameStates.remove(getActiveStateIndex());
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (nextState != null) {
            pushState(gc, nextState);
            nextState = null;
        }

        getActiveState().update(gc, delta);
    }

    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        for (State i_state : gameStates)
            i_state.render(gc, graphics);
    }

    public void mouseClicked(int button, int x, int y, int clickCount) {
        getActiveState().mouseClicked(button, x, y, clickCount);
    }

    public void keyPressed(int key, char c) {
        getActiveState().keyPressed(key, c);
    }

    private State getActiveState() {
        return gameStates.get(getActiveStateIndex());
    }

    private int getActiveStateIndex() {
        return gameStates.size() - 1;
    }

    public void awaitTickPushState(State state) {
        nextState = state;
    }
}
