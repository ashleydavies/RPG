package com.adavieslyons.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.orthorpg.entities.MovingEntity;
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
	
	public MapTile getTile(int x, int y) {
		return MapTile.getTile(tiles[y][x].getId());
	}

	void render(GameContainer gc, Graphics graphics, int totalDelta, boolean renderOccupants) throws SlickException {
		Vector2i screenTL = new Vector2i(0, 0);//screenCoordinatesToTileCoordinates(0, 0);
		Vector2i screenBR = new Vector2i(map.getWidth(), map.getHeight());//screenCoordinatesToTileCoordinates(gc.getWidth(), gc.getHeight());

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
		
		screenTL = new Vector2i(0, 0);
		screenBR = new Vector2i(map.getWidth(), map.getHeight());

		for (int y = screenTL.getY(); y < screenBR.getY(); y++) {
			for (int x = screenTL.getX(); x < screenBR.getX(); x++ ) {
				MapTileData tile = tiles[y][x];
				if (!map.isFogOfWar(x, y) || map.getEditing()) {
					Vector2i renderPosition = map
							.tileCoordinatesToScreenCoordinates(x, y);
					MapTile.getTile(tile.getId()).render(gc, graphics,
							renderPosition.getX(), renderPosition.getY(),
							totalDelta);

					if (((y == 0 || y == tiles.length - 1) || (x == 0 || x == tiles[0].length - 1))
							&& !MapTile.getTile(tile.getId()).getCollision())
						map.mapBorderTexture.draw(renderPosition.getX(),
								renderPosition.getY());
					
					if (renderOccupants && map.areEntitiesVisible(new Vector2i(x, y)))
						tile.renderOccupant(gc, graphics);
				}
			}
		}
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

	public boolean getOccupied(int tX, int tY) {
		return tiles[tY][tX].getOccupied();
	}

	public MapTileData setOccupied(int x, int y, MovingEntity entity) {
		tiles[y][x].setOccupied(entity);
		return tiles[y][x];
	}
}
