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
public class Player extends MovingEntity implements ICombat {
    private MapTileData tileOccupied;
    private GameState game;
    private ItemStack[] items = new ItemStack[27];

    public Player(GameContainer gc, GameState game, Map map, Vector2i position)
            throws SlickException {
        super(map);
        this.game = game;
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
        if (map.getGame().isBattle())
            AP--;
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

    public void gameClicked(int x, int y) {
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


    public int getFortitude()       { return game.getCurrentGameData().getIntSaveData(1); }
    public int getStrength()        { return game.getCurrentGameData().getIntSaveData(2); }
    public int getIntelligence()    { return game.getCurrentGameData().getIntSaveData(3); }
    public int getAgility()         { return game.getCurrentGameData().getIntSaveData(4); }
    public int getPersuasiveness()  { return game.getCurrentGameData().getIntSaveData(5); }
    public int getPerception()      { return game.getCurrentGameData().getIntSaveData(6); }
    public int getLuck()            { return game.getCurrentGameData().getIntSaveData(7); }
    public int getEngineering()     { return game.getCurrentGameData().getIntSaveData(8); }
    public int getSwordsmanship()   { return game.getCurrentGameData().getIntSaveData(9); }
    public int getArchery()         { return game.getCurrentGameData().getIntSaveData(10); }
    public int getSpeed()           { return game.getCurrentGameData().getIntSaveData(11); }
    public int getAnalysis()        { return game.getCurrentGameData().getIntSaveData(12); }
    public int getPickpocketing()   { return game.getCurrentGameData().getIntSaveData(13); }
    public int getCombatMagic()     { return game.getCurrentGameData().getIntSaveData(14); }
    public int getDarkMagic()       { return game.getCurrentGameData().getIntSaveData(15); }
    public int getUtilityMagic()    { return game.getCurrentGameData().getIntSaveData(16); }
    public int getHealingMagic()    { return game.getCurrentGameData().getIntSaveData(17); }
    public int getProtectiveMagic() { return game.getCurrentGameData().getIntSaveData(18); }
}
