package com.adavieslyons.orthorpg.gamestate;

import com.adavieslyons.orthorpg.gamestate.states.DialogState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    List<State> gameStates;
    State nextState;

    public GameStateManager() {
        gameStates = new ArrayList<State>();
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

    public void mouseClicked(int button, int x, int y, int clickCount) throws SlickException {
        getActiveState().mouseClicked(button, x, y, clickCount);
    }

    public void keyPressed(int key, char c) throws SlickException {
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
