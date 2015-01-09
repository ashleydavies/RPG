package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.inventory.ItemStack;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.MapTileData;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * @author Ashley
 */
public class Player extends MovingEntity {
    private MapTileData tileOccupied;
    private ItemStack[] items = new ItemStack[27];

    public Player(GameContainer gc, GameState game, Map map, Vector2i position)
            throws SlickException {
        super(map);
        this.setPosition(position);
        this.setFieldOfView(6);
        tileOccupied = map.setOccupied(position.getX(), position.getY(), this);
        setImage(new Image("img/player.png"));

        items[0] = new ItemStack(0, 1);
        items[1] = new ItemStack(1, 1);
        items[2] = new ItemStack(0, 1);
        items[3] = new ItemStack(0, 1);
        items[5] = new ItemStack(0, 1);
        items[13] = new ItemStack(0, 1);
    }

    @Override
    public void update(GameContainer gc, GameState game, int delta)
            throws SlickException {
        updateMove(delta);
    }

    @Override
    protected void occupiedTileStartChange(Vector2i newTile) {
        tileOccupied = map.setOccupied(newTile.getX(), newTile.getY(), this);
    }

    @Override
    protected void occupiedTileEndChange(Vector2i oldTile) {
        map.setOccupied(oldTile.getX(), oldTile.getY(), null);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        super.render(gc, graphics);
    }

    @Override
    public void onClick(GameState game) {

    }

    public void gameClicked(GameState game, int x, int y) {
        moveToTarget(map.screenCoordinatesToTileCoordinates(game.getInput()
            .getMouseX(), game.getInput().getMouseY()));
    }

    public void onNewMapLoad(Map map, Vector2i newPosition) {
        setNewPosition(newPosition);
        moving = false;
        tileOccupied = map.setOccupied(position.getX(), position.getY(), this);
        this.map = map;
    }

    public ItemStack[] getItems() {
        return items;
    }
}
