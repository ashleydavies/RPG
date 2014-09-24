package com.adavieslyons.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.orthorpg.entities.MovingEntity;

/**
 * 
 * @author Ashley
 */
public class MapTileData {
	private final int id;
	private MovingEntity occupyingEntity;
	protected int data[];

	public MapTileData(int Id) {
		id = Id;
		data = new int[1];
	}

	public void setData(int dataID, int setData) {
		data[dataID] = setData;
	}

	public int getData(int dataID) {
		return data[dataID];
	}
	
	public int getId() {
		return id;
	}
	
	public void setOccupied(MovingEntity occupyingEntity) {
		this.occupyingEntity = occupyingEntity;
	}

	public boolean getOccupied() {
		return occupyingEntity != null;
	}
	
	public void renderOccupant(GameContainer gc, Graphics graphics) throws SlickException {
		if (occupyingEntity != null)
			occupyingEntity.render(gc, graphics);
	}
}
