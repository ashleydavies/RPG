/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sadwhalestudios.util.map.pathfinding;

import com.sadwhalestudios.util.map.Map;

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
}
