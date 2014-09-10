package com.adavieslyons.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

	void render(GameContainer gc, Graphics graphics, int totalDelta) {
		//int y = 0;
		//int x = 0;

		// We can save a lot of rendering here if we calculate the screen tile
		// coordinates
		Vector2i screenTL = map.screenCoordinatesToTileCoordinates(0, 0);
		Vector2i screenBR = map.screenCoordinatesToTileCoordinates(
				gc.getWidth(), gc.getHeight());

		screenTL.add(new Vector2i(1, 1));
		screenBR.add(new Vector2i(2, 2));
		
		if (screenTL.getX() < 0)
			screenTL.setX(0);
		if (screenTL.getY() < 0)
			screenTL.setY(0);
		if (screenBR.getX() > map.getWidth())
			screenBR.setX(map.getWidth());
		if (screenBR.getY() > map.getHeight())
			screenBR.setY(map.getHeight());

		for (int y = screenTL.getY(); y < screenBR.getY(); y++) {
			for (int x = screenTL.getX(); x < screenBR.getX(); x++ ) {
				MapTileData tile = tiles[y][x];
				if (!map.isFogOfWar(x, y) || map.getEditing()) {
					Vector2i renderPosition = map
							.tileCoordinatesToGameCoordinates(x, y);
					MapTile.getTile(tile.getId()).render(gc, graphics,
							renderPosition.getX(), renderPosition.getY(),
							totalDelta);

					if (((y == 0 || y == tiles.length - 1) || (x == 0 || x == tiles[0].length - 1))
							&& !MapTile.getTile(tile.getId()).getCollision())
						map.mapBorderTexture.draw(renderPosition.getX(),
								renderPosition.getY());
				}
			}
		}
		
		/*
		for (MapTileData[] tileRow : tiles) {
			x = 0;

			if (y < screenTL.getY() || y > screenBR.getY()) {
				y++;
				continue;
			}

			for (MapTileData tile : tileRow) {
				if (x < screenTL.getX() || x > screenBR.getX()) {
					x++;
					continue;
				}

				if (!map.isFogOfWar(x, y) || map.getEditing()) {
					Vector2i renderPosition = map
							.tileCoordinatesToGameCoordinates(x, y);
					MapTile.getTile(tile.getId()).render(gc, graphics,
							renderPosition.getX(), renderPosition.getY(),
							totalDelta);

					if (((y == 0 || y == tiles.length - 1) || (x == 0 || x == tileRow.length - 1))
							&& !MapTile.getTile(tile.getId()).getCollision())
						map.mapBorderTexture.draw(renderPosition.getX(),
								renderPosition.getY());
				}
				x++;
			}
			y++;
		}*/
	}

	public void setTile(int tX, int tY, int tileID) {
		tiles[tY][tX] = new MapTileData(tileID);
	}

	public void exportXML(Document document, Element mapRoot) {
		Element layerElement = document.createElement("layer");
		mapRoot.appendChild(layerElement);
		for (MapTileData[] tileRow : tiles) {
			Element rowElement = document.createElement("row");
			layerElement.appendChild(rowElement);
			for (MapTileData tile : tileRow) {
				Element tileElement = document.createElement("tile");
				tileElement.setAttribute("tileID",
						Integer.toString(tile.getId()));
				rowElement.appendChild(tileElement);
			}
		}
	}
}
