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
import com.adavieslyons.util.inventory.Item;
import com.adavieslyons.util.map.Map;

public class GameState extends State {
	Map map;
	private Input input;
	private Input previousInput;
	private Player player;
	private SaveData currentGameData;
	private InventoryGUI inventoryGUI;

	public int WIDTH;
	public int HEIGHT;
	
	private enum InnerState {
		PLAYING,
		INVENTORY
	}
	
	private InnerState state;
	
	public GameState(GameStateManager gsm) {
		super(gsm);
		
		state = InnerState.PLAYING;
	}
	
	@Override
	public void load(GameContainer gc) throws SlickException {
		currentGameData = new SaveData();
		currentGameData.setIntSaveData(0, 50);
		
		input = new Input(gc.getHeight());
		previousInput = new Input(gc.getHeight());
		map = new Map();
		map.load(gc, this);
		player = new Player(gc, this, map);
		inventoryGUI = new InventoryGUI(gc, this);

		Item.LoadItems(this);
	}
	
	@Override
	public void unload() {
		
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		switch (state) {
		case PLAYING:
			if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
				System.exit(0);
			
			if (Keyboard.isKeyDown(Input.KEY_E))
			{
				state = InnerState.INVENTORY;
				break;
			}
			
			WIDTH = gc.getWidth();
			HEIGHT = gc.getHeight();
			
			player.update(gc, this, delta);
			map.update(gc, this, delta);
			break;
		case INVENTORY:
			
			
			inventoryGUI.update(gc, this, delta);
		default:
			break;
		}
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		map.render(gc, graphics);
		player.render(gc, graphics);
		map.renderPostEntities(gc, graphics);
		
		if (state == InnerState.INVENTORY)
			inventoryGUI.render(gc, graphics);
	}
	
	public SaveData getCurrentGameData() {
		return currentGameData;
	}
	
	public Input getInput() {
		return input;
	}
	
	public Input getPreviousInput() {
		return previousInput;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
