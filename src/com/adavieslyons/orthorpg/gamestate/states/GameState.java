package com.adavieslyons.orthorpg.gamestate.states;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.adavieslyons.orthorpg.entities.Player;
import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.State;
import com.adavieslyons.orthorpg.gui.InventoryGUI;
import com.adavieslyons.util.SaveData;
import com.adavieslyons.util.map.Map;

public class GameState extends State {
	Map map;
	private Input input;
	private Player player;
	private SaveData currentGameData;
	private InventoryGUI inventoryGUI;

	public GameState(GameStateManager gsm) {
		super(gsm);
		
	}
	
	@Override
	public void load(GameContainer gc) throws SlickException {
		currentGameData = new SaveData();
		currentGameData.setIntSaveData(0, 50);
		
		input = new Input(gc.getHeight());
		map = new Map();
		map.load(gc, this);
		player = new Player(gc, this, map);
		inventoryGUI = new InventoryGUI(gc, this);
	}
	
	@Override
	public void unload() {
		
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
			System.exit(0);
		
		player.update(gc, this, delta);
		map.update(gc, this, delta);
		inventoryGUI.update(gc, this, delta);
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		map.render(gc, graphics);
		player.render(gc, graphics);
		inventoryGUI.render(gc, graphics);
	}
	
	/**
	 * @return the currentGameData
	 */
	public SaveData getCurrentGameData() {
		return currentGameData;
	}
	
	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
