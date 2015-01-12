package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ashley on 12/01/2015.
 */
public class TurnManager {
    private ArrayList<ICombat> mobs;
    private EntityManager entityManager;
    private GameState game;
    private int turnMobID;

    public TurnManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void initialiseTurn() {
        Mob[] unsortedMobs = new Mob[entityManager.getMobs().size()];
        unsortedMobs = entityManager.getMobs().toArray(unsortedMobs);

        boolean sorted;

        // Sort list of mobs in terms of speed
        do {
            sorted = true;

            for (int i = 0; i < unsortedMobs.length - 1; i++) {
                if (unsortedMobs[i + 1].getSpeed() > unsortedMobs[i].getSpeed()) {
                    Mob mob = unsortedMobs[i + 1];
                    unsortedMobs[i + 1] = unsortedMobs[i];
                    unsortedMobs[i] = mob;

                    sorted = false;
                }
            }
        } while (!sorted);

        // Now sort them and the player into the mobs arraylist
        boolean playerInserted = false;
        mobs = new ArrayList<>();
        for (Mob mob : unsortedMobs) {
            if (mob.getSpeed() > entityManager.getPlayer().getSpeed() || playerInserted) {
                mobs.add(mob);
            } else {
                mobs.add(entityManager.getPlayer());
                playerInserted = true;
                mobs.add(mob);
            }
        }
    }
}
