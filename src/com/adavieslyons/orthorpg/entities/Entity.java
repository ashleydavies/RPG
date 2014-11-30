package com.adavieslyons.orthorpg.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;

/**
 * 
 * @author Ashley
 */
public abstract class Entity {
	protected Map map;
	protected Image image;
	private Vector2f renderPosition;

	public Entity(Map map) {
		this.map = map;

		renderPosition = new Vector2f(0, 0);
	}

	public abstract void update(GameContainer gc, GameState game, int delta)
			throws SlickException;

	public void render(GameContainer gc, Graphics graphics)
			throws SlickException {
		Vector2f renderCoordinatesF = new Vector2f(renderPosition.getX()
				* Game.TILE_SIZE_X, renderPosition.getY() * Game.TILE_SIZE_Y);
		Vector2i renderCoordinates = map
				.screenCoordinatesToGameCoordinates(renderCoordinatesF);
		graphics.drawImage(image, renderCoordinates.getX(),
				renderCoordinates.getY() - Game.TILE_SIZE_Y);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Vector2f getRenderPosition() {
		return renderPosition;
	}

	public void setRenderPosition(Vector2f renderPosition) {
		this.renderPosition = renderPosition;
	}

	public Rectangle getRenderBounds() {
		return new Rectangle(renderPosition.getX() * Game.TILE_SIZE_X,
				renderPosition.getY() * Game.TILE_SIZE_Y, 32, 64);
	}
	
	public boolean mouseOverThis(GameState game) {
		Rectangle rBounds = getRenderBounds();
		return rBounds.contains(game.getInput().getMouseX(), game.getInput().getMouseY());
	}
}
