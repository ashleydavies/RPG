package com.adavieslyons.orthorpg.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.inventory.ItemStack;
import com.adavieslyons.util.map.Map;

/**
 * 
 * @author Ashley
 */
public class Player extends MovingEntity {
	private ItemStack[] items = new ItemStack[27];

	public Player(GameContainer gc, GameState game, Map map)
			throws SlickException {
		super(map);
		image = new Image("img/player.png");

		items[0] = new ItemStack(0, 1);
		items[1] = new ItemStack(1, 1);
		items[2] = new ItemStack(0, 1);
		items[3] = new ItemStack(0, 1);
		items[5] = new ItemStack(0, 1);
		items[13] = new ItemStack(0, 1);
	}

	@Override
	public void update(GameContainer gc, GameState game, int delta)
			throws SlickException {
		if (game.getInput().isMouseButtonDown(0)) {
			moveToTarget(map.screenCoordinatesToTileCoordinates(game.getInput()
					.getMouseX(), game.getInput().getMouseY()));
		}

		updateMove(delta);

		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				int tX = getOccupiedPosition().getX() + x;
				int tY = getOccupiedPosition().getY() + y;

				if (tX >= 0 && tY >= 0)
					if (tX < map.getWidth() && tY < map.getHeight())
						map.revealCoordinate(tX, tY);
			}
		}
	}

	@Override
	public void render(GameContainer gc, Graphics graphics)
			throws SlickException {
		super.render(gc, graphics);
	}

	public ItemStack[] getItems() {
		return items;
	}
}
