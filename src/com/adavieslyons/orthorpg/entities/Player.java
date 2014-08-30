package com.adavieslyons.orthorpg.entities;

import org.newdawn.slick.*;
import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.map.Map;

/**
 * 
 * @author Ashley
 */
public class Player extends MovingEntity {
	Image avatar;

	public Player(GameContainer gc, GameState game, Map map) throws SlickException {
		super(map);
		avatar = new Image("img/player.png");
	}
	
	@Override
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		if (game.getInput().isMouseButtonDown(0) && !moving)
		{
			int tX = (int)(game.getInput().getMouseX() / Game.TILE_SIZE);
			int tY = (int)(game.getInput().getMouseY() / Game.TILE_SIZE);
			
			moveToTarget(tX, tY);
		}

		updateMove(delta);
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		graphics.drawImage(avatar, (int) (getRenderPosition().getX() * Game.TILE_SIZE), (int) (getRenderPosition().getY() * Game.TILE_SIZE - 32));
	}
	
	public Image getAvatar() {
		return avatar;
	}
}
