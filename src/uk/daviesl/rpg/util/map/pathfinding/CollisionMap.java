package uk.daviesl.rpg.util.map.pathfinding;

import uk.daviesl.rpg.util.map.Map;

/**
 * @author Ashley
 */
public class CollisionMap {
    private final boolean[][] collisionData;
    private final Map map;

    public CollisionMap(Map map) {
        collisionData = new boolean[map.getHeight()][map.getWidth()];
        this.map = map;

        for (int y = 0; y < collisionData.length; y++) {
            for (int x = 0; x < collisionData[y].length; x++) {
                collisionData[y][x] = map.getCollideable(x, y);
            }
        }
    }

    public boolean getOccupied(int x, int y) {
        return map.getOccupied(x, y);
    }

    public boolean getCollision(int x, int y) {
        return collisionData[y][x];
    }

    public int getRows() {
        return collisionData.length;
    }

    public int getColumns() {
        return collisionData[0].length;
    }
}
