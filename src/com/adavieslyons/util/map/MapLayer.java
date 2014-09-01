package com.adavieslyons.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.adavieslyons.util.Vector2i;

/**
 * 
 * @author Ashley
 */
public class MapLayer {
	MapTileData tiles[][];
	Map map;

	public MapLayer(MapTileData tiles[][], Map map) {
		this.map = map;
		this.tiles = tiles;
	}

	public boolean getCollideable(int y, int x) {
		return MapTile.getTile(tiles[y][x].getId()).getCollision();
	}

	void render(GameContainer gc, Graphics graphics) {
		int y = 0;
		int x = 0;

		for (MapTileData[] tileRow : tiles) {
			x = 0;
			for (MapTileData tile : tileRow) {
				if (!map.isFogOfWar(x, y)) {
					Vector2i renderPosition = map
							.tileCoordinatesToGameCoordinates(x, y);
					MapTile.getTile(tile.getId()).render(gc, graphics,
							renderPosition.getX(), renderPosition.getY());
				}
				x++;
			}
			y++;
		}
	}
}
