package uk.daviesl.rpg.util.xml;

import uk.daviesl.rpg.entities.Mob;
import uk.daviesl.rpg.util.map.MapLayer;

import java.util.List;

/**
 * Created by Ashley on 25/02/2016.
 */
public class MapData {
    private int height;
    private int width;
    private MapLayer[] layers;
    private List<Mob> mobs;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public MapLayer[] getLayers() {
        return layers;
    }

    public void setLayers(MapLayer[] layers) {
        this.layers = layers;
    }

    public List<Mob> getMobs() {
        return mobs;
    }

    public void setMobs(List<Mob> mobs) {
        this.mobs = mobs;
    }
}
