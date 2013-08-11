package com.sadwhalestudios.orthorpg.gamestate;

import java.util.List;

public class GameStateManager {
	List<GameState> gameStates;
	
	public void setState(GameState state) {
		for (GameState i_state: gameStates) {
			i_state.unload();
		}
		
		gameStates.clear();
		gameStates.add(state);
	}
	
	public void pushState(GameState state) {
		gameStates.add(state);
	}
	
	public void popState() {
		gameStates.remove(getActiveStateIndex());
	}
	
	public void update() {
		getActiveState().update();
	}
	
	public void render() {
		for (GameState i_state: gameStates)
			i_state.render();
	}
	
	private GameState getActiveState() {
		return gameStates.get(getActiveStateIndex());
	}
	
	private int getActiveStateIndex() {
		return gameStates.size() - 1;
	}
}
