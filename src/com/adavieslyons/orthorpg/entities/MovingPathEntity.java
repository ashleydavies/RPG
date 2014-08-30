package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;

public abstract class MovingPathEntity extends MovingEntity {
	private int currentPathPosition = 0;
	private Vector2i path[];

	public MovingPathEntity(Map map) {
		super(map);
	}
	
	protected void updatePath() {
		if (!moving)
		{
			if (getOccupiedPosition().equals(path[currentPathPosition]))
			{
				currentPathPosition++;
				
				if (currentPathPosition >= path.length)
					currentPathPosition = 0;
			}
			
			moveToTarget(path[currentPathPosition].getX(), path[currentPathPosition].getY());
		}

	}

	public void setPath(Vector2i[] path) {
		this.path = path;
	}
}
