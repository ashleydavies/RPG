package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.pathfinding.AStar;

import java.util.List;

public abstract class MovingEntity extends Entity {
    private Vector2i desiredPosition;
    private int fieldOfView;
    protected boolean moving;

    float tileMoveTimer = 1;
    private float tileMoveCurrently = 0;
    protected int AP = 7;

    public MovingEntity(Map map) {
        super(map);

        desiredPosition = new Vector2i(0, 0);
    }

    private void pathfind(int tileX, int tileY) {
        if (tileX == position.getX()
                && tileY == position.getY())
            return;

        try {
            List<AStar.Node> path = AStar.path(map.getNodeMatrix()[position
                            .getY()][position.getX()],
                    map.getNodeMatrix()[tileY][tileX]);

            if (path != null && !path.isEmpty()) {
                lastPosition = position;
                position = new Vector2i(path.get(0).getX(), path.get(0)
                        .getY());
                occupiedTileStartChange(position);
                moving = true;
            }
        } catch (Exception e) {
            System.out.println("Pathfinding issue!");
            // If they're having issues pathfinding, set their AP to 0 to avoid weird bugs
            // if there is just the player and glitched entities they will infinitely hold up
            // the battle queue
            this.AP = 0;
        }
    }

    public void moveToTarget(int tX, int tY) {
        if (tX < map.getWidth() && tY < map.getHeight() && tX >= 0 && tY >= 0)
            if (!map.getCollideable(tX, tY)) {
                desiredPosition = new Vector2i(tX, tY);
            }
    }

    public void moveToTarget(Vector2i target) {
        moveToTarget(target.getX(), target.getY());
    }

    protected void updateMove(int delta) {
        if (moving) {
            tileMoveCurrently += ((float) delta / 250);
            setPositionLerpFraction(tileMoveCurrently);

            if (tileMoveCurrently >= 1) {
                tileMoveCurrently = 0;
                moving = false;
                occupiedTileEndChange(lastPosition);
                setPositionLerpFrom(position);
                setPosition(position);
                setPositionLerpFraction(0);
            }
        }

        if (!moving && desiredPosition != position && (AP > 0 || !map.getGame().isBattle()))
            pathfind(desiredPosition.getX(), desiredPosition.getY());
    }

    protected void setNewPosition(Vector2i newPosition) {
        lastPosition = newPosition;
        setPosition(newPosition);
        setPositionLerpFrom(newPosition);
        setDesiredPosition(newPosition);
        setPositionLerpFraction(1);
    }

    public Vector2i getDesiredPosition() {
        return desiredPosition;
    }

    public void setDesiredPosition(Vector2i desiredPosition) {
        this.desiredPosition = desiredPosition;
    }

    public int getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(int fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    protected abstract void occupiedTileStartChange(Vector2i newTile);

    protected abstract void occupiedTileEndChange(Vector2i oldTile);
}
