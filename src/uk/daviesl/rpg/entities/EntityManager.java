package uk.daviesl.rpg.entities;

import uk.daviesl.rpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;

import java.util.ArrayList;

public class EntityManager {
    private ArrayList<Mob> mobs;
    private Player player;
    private final GameState game;
    private final TurnManager turnManager;

    public EntityManager(GameState game) {
        this.mobs = new ArrayList<>();
        this.game = game;
        this.turnManager = new TurnManager(this);
    }

    public void update(GameContainer gc, GameState game, int delta) {
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

    public ArrayList<Mob> getMobs() {
        return mobs;
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
