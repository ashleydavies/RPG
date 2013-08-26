package com.sadwhalestudios.orthorpg.gamestate;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class GameStateManager {
	List<State> gameStates;
	
	public GameStateManager() {
		gameStates = new ArrayList<State>();
	}
	
	public void setState(GameContainer gc, State state) throws SlickException {
		for (State i_state: gameStates) {
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
		gameStates.remove(getActiveStateIndex());
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		getActiveState().update(gc, delta);
	}
	
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		for (State i_state: gameStates)
			i_state.render(gc, graphics);
	}
	
	private State getActiveState() {
		return gameStates.get(getActiveStateIndex());
	}
	
	private int getActiveStateIndex() {
		return gameStates.size() - 1;
	}
}
