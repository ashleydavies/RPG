package com.adavieslyons.util.map.pathfinding;

import com.adavieslyons.util.map.Map;

/**
 *
 * @author
 * Ashley
 */
public class CollisionMap {
    boolean collisionData[][];
    
    public CollisionMap(Map map)
    {
    	collisionData = new boolean[map.getHeight()][map.getWidth()];
    	
    	int y = 0;
    	
    	for (boolean[] column: collisionData)
    	{
    		for (int x = 0; x < column.length; x++)
    		{
    			column[x] = false;
    			
    			if (map.getCollideable(y, x))
    			{
    				column[x] = true;
    			}
    		}
    		
    		y++;
    	}
    }
    
    public boolean getCollision(int x, int y)
    {
    	return collisionData[y][x];
    }
    
    public int getRows()
    {
    	return collisionData.length;
    }
    
    public int getColumns()
    {
    	return collisionData[0].length;
    }
}
