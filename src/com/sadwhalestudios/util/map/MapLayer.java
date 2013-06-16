package com.sadwhalestudios.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 *
 * @author
 * Ashley
 */
public class MapLayer {
    MapTileData tiles[][];
    
    public MapLayer(MapTileData Tiles[][])
    {
        tiles = Tiles;
    }

    void render(GameContainer gc, Graphics graphics) {
        int y = 0;
        int x = 0;
        
        for (MapTileData[] tileRow: tiles)
        {
            x = 0;
            for (MapTileData tile: tileRow)
            {
                MapTile.getTile(tile.getId()).render(gc, graphics, x * 32, y * 32);
                x++;
            }
            y++;
        }
    }
}
