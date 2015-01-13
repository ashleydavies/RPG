package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.map.Map;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

public class EntityManager {
    // We maintain a list of mobs as they have varying actions if in battle
    private ArrayList<Mob> mobs;
    // And store the player separately too, for easier access
    private Player player;
    private Map map;
    private GameState game;
    private TurnManager turnManager;

    public EntityManager(Map map, GameState game) {
        this.mobs = new ArrayList<Mob>();
        this.map = map;
        this.game = game;
        this.turnManager = new TurnManager(this);
    }

    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        if (game.isBattle()) {
            turnManager.update(gc, delta);
        } else {
            for (Mob mob : mobs) {
                mob.update(gc, game, delta);
            }
        }
    }

    public GameState getGame() {
        return game;
    }

    public void clear() {
        mobs = new ArrayList<>();
    }

    public Entity getMobFromScreenCoordinates(int x, int y) {
        for (Mob mob : mobs)
            if (mob.areScreenCoordinatesOnEntity(x, y))
                return mob;
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Mob> getMobs() { return mobs; }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addMob(Mob mob) {
        mobs.add(mob);
    }

    public void removeMob(Mob mob) {
        mobs.remove(mob);
    }
}
