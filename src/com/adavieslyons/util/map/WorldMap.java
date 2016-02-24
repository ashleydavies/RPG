package com.adavieslyons.util.map;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.FileLoader;
import org.newdawn.slick.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WorldMap {
    private Image iconBackground;
    private Image iconHoverBackground;
    private Image iconSelectedBackground;
    private Image iconUnclearedBackground;
    private Image iconMapApproach;
    private Image[] icons;
    private Image worldMap;
    private MapIconData[] mapIconData;
    private MapConnection[] mapConnectors;
    private MapIconData selectedIcon;
    private MapDirection selectedApproachDirection;

    public WorldMap() {
        Image icons = FileLoader.getImage("mapIcons");

        this.icons = new Image[(icons.getWidth() / 32) - 1];

        for (int i = 0; i < icons.getWidth() / 32 - 1; i++) {
            this.icons[i] = icons.getSubImage((i + 1) * 32, 0, 32, 32);
        }

        this.iconBackground = icons.getSubImage(0, 0, 32, 32);
        this.iconHoverBackground = icons.getSubImage(0, 32, 32, 32);
        this.iconSelectedBackground = icons.getSubImage(0, 64, 32, 32);
        this.iconUnclearedBackground = icons.getSubImage(0, 96, 32, 32);
        this.iconMapApproach = icons.getSubImage(32, 96, 32, 32);
        this.worldMap = FileLoader.getImage("worldmap");

        // Now load the map icon data
        Document document = FileLoader.getXML("map/mapList");

        NodeList mapNodes = document.getElementsByTagName("map");
        this.mapIconData = new MapIconData[mapNodes.getLength()];
        for (int i = 0; i < mapNodes.getLength(); i++) {
            Element mapNode = (Element) mapNodes.item(i);
            MapIconData newIconData = new MapIconData(
                    mapNode.getAttribute("name"), i, Integer.parseInt(mapNode
                    .getAttribute("icon")), Integer.parseInt(mapNode
                    .getAttribute("mX")), Integer.parseInt(mapNode
                    .getAttribute("mY")));

            this.mapIconData[i] = newIconData;
        }

        NodeList connectionNodes = document.getElementsByTagName("connection");
        this.mapConnectors = new MapConnection[connectionNodes.getLength()];
        for (int i = 0; i < connectionNodes.getLength(); i++) {
            Element connectionNode = (Element) connectionNodes.item(i);
            MapConnection connection = new MapConnection(
                    this.mapIconData[Integer.parseInt(connectionNode
                            .getAttribute("map1"))],
                    this.mapIconData[Integer.parseInt(connectionNode
                            .getAttribute("map2"))],
                    MapDirection.valueOf(connectionNode.getAttribute("dir")
                            .toUpperCase()));
            mapConnectors[i] = connection;
        }

        leavingMapArea(1, MapDirection.NORTH);
    }

    // Fired when player leaves a map into the world map
    public void leavingMapArea(int fromMap, MapDirection leavingDirection) {
        mapIconData[fromMap].setCleared(true);
        MapIconData map = mapIconData[fromMap];
        // We should have a connection from this on the direction
        MapDirection oppositeDirection = MapDirection
                .getOpposite(leavingDirection);
        for (MapConnection connection : mapConnectors) {
            if ((connection.map1 == map && connection.direction == leavingDirection)
                    || (connection.map2 == map && connection.direction == oppositeDirection)) {
                MapIconData mapNew = connection.map1;
                if (connection.map1 == map)
                    mapNew = connection.map2;
                setSelectedIcon(mapNew, oppositeDirection);
            }
        }
    }

    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        for (MapIconData mapIcon : mapIconData) {
            mapIcon.update(gc, game, delta);
        }

        if (game.getInput().isKeyDown(Input.KEY_ENTER) && selectedIcon != null)
            game.loadMap(gc, selectedIcon.getMapID(), selectedApproachDirection);
    }

    public void render(GameContainer gc, Graphics graphics) {
        graphics.drawImage(worldMap, 3, 3);

        // Render connections
        for (MapConnection connection : mapConnectors) {
            if (connection.map1.isCleared() || connection.map2.isCleared())
                graphics.setColor(Color.green);
            else
                graphics.setColor(Color.red);
            graphics.drawLine(connection.getMap1().getmX() + 16, connection
                            .getMap1().getmY() + 16, connection.getMap2().getmX() + 16,
                    connection.getMap2().getmY() + 16);
            graphics.setColor(Color.black);
        }

        for (MapIconData mapIcon : mapIconData) {
            mapIcon.render(gc, graphics, icons[mapIcon.getIconID()]);
        }
    }

    private void setSelectedIcon(MapIconData icon) {
        MapDirection approachDirection = MapDirection.NORTH;

        for (MapConnection connection : mapConnectors) {
            if (connection.map1 == icon) {
                approachDirection = connection.direction;
            } else if (connection.map2 == icon) {
                approachDirection = MapDirection.getOpposite(connection.direction);
            }
        }

        setSelectedIcon(icon, approachDirection);
    }

    private void setSelectedIcon(MapIconData icon, MapDirection approachDirection) {
        this.selectedIcon = icon;
        this.selectedApproachDirection = approachDirection;
    }

    public enum MapDirection {
        NORTH, EAST, SOUTH, WEST;

        public static MapDirection getOpposite(MapDirection direction) {
            switch (direction) {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
            }
            return null;
        }
    }

    private class MapConnection {
        private final MapIconData map1;
        private final MapIconData map2;
        private final MapDirection direction;

        public MapConnection(MapIconData map1, MapIconData map2,
                             MapDirection direction) {
            this.map1 = map1;
            this.map2 = map2;
            this.direction = direction;
        }

        public MapIconData getMap1() {
            return map1;
        }

        public MapIconData getMap2() {
            return map2;
        }

        public MapDirection getDirection() {
            return direction;
        }
    }

    private class MapIconData {
        private final int mapID;
        private int iconID;
        private String name;
        private int mX, mY;
        private int tX, tY; // Tooltip coordinates
        private boolean hovered;
        private boolean cleared;

        public MapIconData(String name, int mapID, int iconID, int mX, int mY) {
            this.name = name;
            this.mapID = mapID;
            this.iconID = iconID;
            this.mX = mX;
            this.mY = mY;
        }

        public void update(GameContainer gc, GameState game, int delta) {
            if (game.getInput().getMouseX() >= mX
                    && game.getInput().getMouseX() <= mX + 32
                    && game.getInput().getMouseY() >= mY
                    && game.getInput().getMouseY() <= mY + 32) {
                hovered = true;
                tX = game.getInput().getMouseX() + 16;
                tY = game.getInput().getMouseY() + 16;
            } else
                hovered = false;

            if (hovered && game.getInput().isMouseButtonDown(0))
                setSelectedIcon(this);
        }

        public void render(GameContainer gc, Graphics graphics, Image icon) {
            if (selectedIcon == this) {
                graphics.drawImage(iconSelectedBackground, mX, mY);
                switch (selectedApproachDirection) {
                    case NORTH:
                        graphics.rotate(mX + 16, mY - 32 + 16, 180);
                        graphics.drawImage(iconMapApproach, mX, mY - 32);
                        graphics.resetTransform();
                        break;
                    case EAST:
                        graphics.rotate(mX + 32 + 16, mY + 16, 270);
                        graphics.drawImage(iconMapApproach, mX + 32, mY);
                        graphics.resetTransform();
                        break;
                    case SOUTH:
                        graphics.drawImage(iconMapApproach, mX, mY + 32);
                        break;
                    case WEST:
                        graphics.rotate(mX - 32 + 16, mY + 16, 90);
                        graphics.drawImage(iconMapApproach, mX - 32, mY);
                        graphics.resetTransform();
                        break;
                }
            } else if (!cleared) {
                graphics.drawImage(iconUnclearedBackground, mX, mY);
            } else if (hovered) {
                graphics.drawImage(iconHoverBackground, mX, mY);
            } else
                graphics.drawImage(iconBackground, mX, mY);
            graphics.drawImage(icon, mX, mY);

            if (hovered) {
                graphics.drawString(name, tX, tY);
            }
        }

        public int getIconID() {
            return iconID;
        }

        public void setIconID(int iconID) {
            this.iconID = iconID;
        }

        public int getMapID() {
            return mapID;
        }

        public boolean isCleared() {
            return cleared;
        }

        public void setCleared(boolean cleared) {
            this.cleared = cleared;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getmX() {
            return mX;
        }

        public void setmX(int mX) {
            this.mX = mX;
        }

        public int getmY() {
            return mY;
        }

        public void setmY(int mY) {
            this.mY = mY;
        }
    }
}
