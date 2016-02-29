package uk.daviesl.rpg.entities;

import uk.daviesl.rpg.util.Vector2i;
import uk.daviesl.rpg.util.map.Map;

public abstract class MovingPathEntity extends MovingEntity {
    private int currentPathPosition = 0;
    private Vector2i path[];

    public MovingPathEntity(Map map) {
        super(map);
    }

    protected void updatePath() {
        if (!moving) {
            if (getPosition().equals(path[currentPathPosition])) {
                currentPathPosition++;

                if (currentPathPosition >= path.length)
                    currentPathPosition = 0;
            }

            moveToTarget(path[currentPathPosition].getX(),
                    path[currentPathPosition].getY());
        }

    }

    public void setPath(Vector2i[] path) {
        this.path = path;
    }
}
