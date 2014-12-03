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
	// In terms of tiles
	protected Vector2i position;
	protected Vector2i positionLerpFrom;
	private float positionLerpFraction;

	public Entity(Map map) {
		this.map = map;

		position = new Vector2i(0, 0);
		positionLerpFrom = new Vector2i(0, 0);
	}

	public abstract void update(GameContainer gc, GameState game, int delta)
			throws SlickException;

	public void render(GameContainer gc, Graphics graphics)
			throws SlickException {
		Vector2i positionCoordinates = map.tileCoordinatesToGameCoordinates(position);
		Vector2i lerpFromCoordinates = map.tileCoordinatesToGameCoordinates(positionLerpFrom);
		Vector2f drawCoordinates = lerpFromCoordinates.lerpTo(positionCoordinates, positionLerpFraction);
		graphics.drawImage(image, (int)(drawCoordinates.getX() + Game.TILE_SIZE_X / 2 - image.getWidth() / 2), (int)(drawCoordinates.getY() - image.getHeight() / 2 - Game.TILE_SIZE_Y / 2));
		/*Vector2f renderCoordinatesF = new Vector2f(renderPosition.getX()
				* Game.TILE_SIZE_X, renderPosition.getY() * Game.TILE_SIZE_Y);
		Vector2i tileCoordinates = new Vector2i((int) renderPosition.getX(), (int) renderPosition.getY());
		Vector2i drawCoordinates = map.tileCoordinatesToGameCoordinates(tileCoordinates);
		Vector2f offset = new Vector2f(renderCoordinatesF.getX() - drawCoordinates.getX(), renderCoordinatesF.getY() - drawCoordinates.getY());
		System.out.println(offset);
		graphics.drawImage(image, drawCoordinates.getX() + Game.TILE_SIZE_X / 2 - image.getWidth() / 2 + offset.getX() * 2,
								  drawCoordinates.getY() - image.getHeight() / 2 - (Game.TILE_SIZE_Y / 2) + offset.getY());*/
	}

	public Image getImage() {
		return image;
	}
	
	public Vector2i getPositionLerpFrom() {
		return positionLerpFrom;
	}

	public void setPositionLerpFrom(Vector2i positionLerpFrom) {
		this.positionLerpFrom = positionLerpFrom;
	}

	public void setPosition(Vector2i position) {
		this.position = position;
	}

	public Vector2i getPosition() {
		return position;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Vector2f getRenderPosition() {
		Vector2i positionCoordinates = map.tileCoordinatesToGameCoordinates(position);
		Vector2i lerpFromCoordinates = map.tileCoordinatesToGameCoordinates(positionLerpFrom);
		Vector2f drawCoordinates = lerpFromCoordinates.lerpTo(positionCoordinates, positionLerpFraction);
		return drawCoordinates;
	}
	
	public void setPositionLerpFraction(float positionLerpFraction) {
		this.positionLerpFraction = positionLerpFraction;
	}

	public Rectangle getRenderBounds() {
		return new Rectangle(getRenderPosition().getX(),
				getRenderPosition().getY(), 32, 64);
	}
	
	public boolean mouseOverThis(GameState game) {
		Rectangle rBounds = getRenderBounds();
		return rBounds.contains(game.getInput().getMouseX(), game.getInput().getMouseY());
	}
}
