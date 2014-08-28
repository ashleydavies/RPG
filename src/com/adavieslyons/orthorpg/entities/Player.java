package com.adavieslyons.orthorpg.entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Vector2f;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.InventoryGUI;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.pathfinding.AStar;

/**
 * 
 * @author Ashley
 */
public class Player extends Entity {
	Image image;
	InventoryGUI inventoryGUI;
	
	private Vector2i previousPosition;
	private Vector2i occupiedPosition;
	private Vector2f renderPosition;
	private Vector2i desiredPosition;
	private final Map map;
	private boolean moving = false;
	float tileMoveTimer = 1;
	float tileMoveCurrently = 0;
	
	public Player(GameContainer gc, GameState game, Map map) throws SlickException {
		previousPosition = new Vector2i(0, 0);
		occupiedPosition = new Vector2i(0, 0);
		desiredPosition = new Vector2i(0, 0);
		renderPosition = new Vector2f(0, 0);
		image = new Image("img/player.png");
		inventoryGUI = new InventoryGUI(gc, game);
		this.map = map;
	}
	
	@Override
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		if (moving) {
			tileMoveCurrently += ((float) delta / 250);
			renderPosition = previousPosition.lerpTo(occupiedPosition, tileMoveCurrently);
			
			if (tileMoveCurrently >= 1) {
				tileMoveCurrently = 0;
				moving = false;
				renderPosition = new Vector2f(occupiedPosition.getX(), occupiedPosition.getY());
			}
		}
		
		if (game.getInput().isMouseButtonDown(0) && !moving)
		{
			int tX = (int)(game.getInput().getMouseX() / Game.TILE_SIZE);
			int tY = (int)(game.getInput().getMouseY() / Game.TILE_SIZE);
			if (tX < map.getWidth() && tY < map.getHeight())
				if (!map.getCollideable(tX, tY))
				{
					pathfind(tX, tY);
					desiredPosition = new Vector2i(tX, tY);
				}
		}
		
		if (!moving && desiredPosition != occupiedPosition)
			pathfind(desiredPosition.getX(), desiredPosition.getY());
		
		inventoryGUI.update(gc, game, delta);
	}
	
	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		graphics.drawImage(image, (int) (renderPosition.getX() * Game.TILE_SIZE), (int) (renderPosition.getY() * Game.TILE_SIZE - 32));
		inventoryGUI.render(gc, graphics);
	}
	
	public void pathfind(int tileX, int tileY) {
		if (tileX == occupiedPosition.getX() && tileY == occupiedPosition.getY())
			return;
		
		List<AStar.Node> path = AStar.path(map.getNodeMatrix()[occupiedPosition.getY()][occupiedPosition.getX()], map.getNodeMatrix()[tileY][tileX]);
		
		if (!path.isEmpty()) {
			previousPosition = occupiedPosition;
			occupiedPosition = new Vector2i(path.get(0).getX(), path.get(0).getY());
			moving = true;
		}
	}
	
	public Vector2f getRenderPosition() {
		return renderPosition;
	}
	
	public void setRenderPosition(Vector2f renderPosition) {
		this.renderPosition = renderPosition;
	}
	
	public Vector2i getOccupiedPosition() {
		return occupiedPosition;
	}
	
	public void setOccupiedPosition(Vector2i occupiedPosition) {
		this.occupiedPosition = occupiedPosition;
	}
	
	public Vector2i getPreviousPosition() {
		return previousPosition;
	}
	
	public void setPreviousPosition(Vector2i previousPosition) {
		this.previousPosition = previousPosition;
	}

	public Vector2i getDesiredPosition() {
		return desiredPosition;
	}

	public void setDesiredPosition(Vector2i desiredPosition) {
		this.desiredPosition = desiredPosition;
	}
}
