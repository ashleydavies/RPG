package uk.daviesl.rpg.util.map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.daviesl.rpg.entities.MovingEntity;
import uk.daviesl.rpg.util.Vector2i;

/**
 * @author Ashley
 */
public class MapLayer {
    private final MapTileData[][] tiles;

    public MapLayer(MapTileData tiles[][]) {
        this.tiles = tiles;
    }

    public boolean getCollideable(int y, int x) {
        return MapTile.getTile(tiles[y][x].getId()).getCollision();
    }

    public MapTile getTile(int x, int y) {
        return MapTile.getTile(tiles[y][x].getId());
    }

    void render(GameContainer gc, Graphics graphics, Map map, int totalDelta, boolean renderOccupants) throws SlickException {
        Vector2i minXTile = map.screenCoordinatesToTileCoordinates(0, 0);
        Vector2i minYTile = map.screenCoordinatesToTileCoordinates(gc.getWidth(), 0);
        Vector2i maxYTile = map.screenCoordinatesToTileCoordinates(0, gc.getHeight());
        Vector2i maxXTile = map.screenCoordinatesToTileCoordinates(gc.getWidth(), gc.getHeight());
        if (minXTile.getX() < 0) {
            minXTile.setX(0);
        } else if (maxXTile.getX() > map.getWidth()) {
            maxXTile.setX(map.getWidth());
        }

        if (minYTile.getY() < 0) {
            minYTile.setY(0);
        } else  if (maxYTile.getY() > map.getHeight()) {
            maxYTile.setY(map.getHeight());
        }

        for (int y = minYTile.getY(); y < maxYTile.getY(); y++) {
            for (int x = minXTile.getX(); x < maxXTile.getX(); x++) {
                MapTileData tile = tiles[y][x];

                Vector2i renderPosition = map
                        .tileCoordinatesToScreenCoordinates(x, y);
                MapTile.getTile(tile.getId()).render(gc, graphics,
                        renderPosition.getX(), renderPosition.getY(),
                        totalDelta);

                if (((y == 0 || y == tiles.length - 1) || (x == 0 || x == tiles[0].length - 1))
                        && !MapTile.getTile(tile.getId()).getCollision()) {
                    map.getBorderTexture().draw(renderPosition.getX(),
                            renderPosition.getY());
                }

                if (renderOccupants) {
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
