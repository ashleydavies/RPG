package com.sadwhalestudios.orthorpg.gamestate;

public abstract class GameState {
	public abstract void load();
	public abstract void unload();
	
	public abstract void update();
	public abstract void render();
}
