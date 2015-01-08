package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

public class EntityManager {
    // We maintain a list of mobs as they have varying actions if in battle
    private ArrayList<Mob> mobs;
    // And store the player separately too, for easier access
    private Player player;

    public EntityManager() {
        mobs = new ArrayList<Mob>();
    }

    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        for (Mob mob : mobs) {
            mob.update(gc, game, delta);
        }
    }

    public Player getPlayer() {
        return player;
    }

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
