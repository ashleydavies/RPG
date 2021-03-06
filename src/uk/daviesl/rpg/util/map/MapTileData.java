package uk.daviesl.rpg.util.map;

import uk.daviesl.rpg.entities.MovingEntity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * @author Ashley
 */
public class MapTileData {
    private final int id;
    private final int[] data;
    private MovingEntity occupyingEntity;

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

    public boolean getOccupied() {
        return occupyingEntity != null;
    }

    public void setOccupied(MovingEntity occupyingEntity) {
        this.occupyingEntity = occupyingEntity;
    }

    public void renderOccupant(GameContainer gc, Graphics graphics) throws SlickException {
        if (occupyingEntity != null)
            occupyingEntity.render(gc, graphics);
    }
}
