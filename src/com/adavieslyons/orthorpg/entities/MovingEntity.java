package com.adavieslyons.orthorpg.entities;

import java.util.List;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.adavieslyons.orthorpg.Game;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.pathfinding.AStar;

public abstract class MovingEntity extends Entity {
	private Vector2i previousPosition;
	private Vector2i occupiedPosition;
	private Vector2f renderPosition;
	private Vector2i desiredPosition;
	protected boolean moving;
	protected final Map map;
	
	float tileMoveTimer = 1;
	float tileMoveCurrently = 0;
	
	public MovingEntity(Map map) {
		previousPosition = new Vector2i(0, 0);
		occupiedPosition = new Vector2i(0, 0);
		desiredPosition = new Vector2i(0, 0);
		renderPosition = new Vector2f(0, 0);

		this.map = map;
	}

	
	private void pathfind(int tileX, int tileY) {
		if (tileX == occupiedPosition.getX() && tileY == occupiedPosition.getY())
			return;
		
		List<AStar.Node> path = AStar.path(map.getNodeMatrix()[occupiedPosition.getY()][occupiedPosition.getX()], map.getNodeMatrix()[tileY][tileX]);
		
		if (!path.isEmpty()) {
			previousPosition = occupiedPosition;
			occupiedPosition = new Vector2i(path.get(0).getX(), path.get(0).getY());
			moving = true;
		}
	}
	
	public void moveToTarget(int tX, int tY) {
		if (tX < map.getWidth() && tY < map.getHeight())
			if (!map.getCollideable(tX, tY))
			{
				pathfind(tX, tY);
				desiredPosition = new Vector2i(tX, tY);
			}
	}
	
	protected void updateMove(int delta) {
		if (moving) {
			tileMoveCurrently += ((float) delta / 250);
			renderPosition = previousPosition.lerpTo(occupiedPosition, tileMoveCurrently);
			
			if (tileMoveCurrently >= 1) {
				tileMoveCurrently = 0;
				moving = false;
				renderPosition = new Vector2f(occupiedPosition.getX(), occupiedPosition.getY());
			}
		}

		if (!moving && desiredPosition != occupiedPosition)
			pathfind(desiredPosition.getX(), desiredPosition.getY());
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
	
	public Rectangle getRenderBounds() {
		return new Rectangle(renderPosition.getX() * Game.TILE_SIZE, renderPosition.getY() * Game.TILE_SIZE, 32, 64);
	}
}
