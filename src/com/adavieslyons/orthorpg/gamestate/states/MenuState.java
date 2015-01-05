package com.adavieslyons.orthorpg.gamestate.states;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.State;

public class MenuState extends State {
	public MenuState(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}
	
	Image buttonTexture;
	Image headerImage;
	
	String[] buttons = {"Play", "Settings", "Help", "Exit"};

	@Override
	public void load(GameContainer gc) throws SlickException {
		buttonTexture = new Image("img/button.png");
		headerImage = new Image("img/header.png");
	}

	@Override
	public void unload() {
		
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if (Keyboard.isKeyDown(Input.KEY_ENTER))
			gameStateManager.setState(gc, new GameState(gameStateManager));
	}

	@Override
	public void render(GameContainer gc, Graphics graphics) {
		graphics.setBackground(Color.darkGray);
		graphics.drawImage(headerImage, gc.getWidth() / 2 - headerImage.getWidth() / 2, 10);
		
		int bNo = 0;
		
		for (String button : buttons) {
			graphics.drawImage(buttonTexture, gc.getWidth() / 2 - buttonTexture.getWidth() / 2, 220 + 120 * bNo);
			graphics.setColor(Color.black);
			graphics.drawString(button, gc.getWidth() / 2 - graphics.getFont().getWidth(button) / 2, 255 + 120 * bNo);
			bNo++;
		}
	}
}
