package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.lwjgl.Sys;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ashley on 12/01/2015.
 */
public class TurnManager {
    private ArrayList<ITakeTurns> mobs;
    private EntityManager entityManager;
    private int turnMobID = -1;

    public TurnManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void initialiseTurn() {
        entityManager.getPlayer().setAP(entityManager.getPlayer().getMaxAP());
        entityManager.getPlayer().setDesiredPosition(entityManager.getPlayer().getPosition());
        System.out.println("Initialising turn");

        ITakeTurns[] unsortedMobs = new ITakeTurns[entityManager.getMobs().size()];
        unsortedMobs = entityManager.getMobs().toArray(unsortedMobs);

        boolean sorted;

        // Sort list of mobs in terms of speed
        do {
            sorted = true;

            for (int i = 0; i < unsortedMobs.length - 1; i++) {
                if (unsortedMobs[i + 1].getSpeed() > unsortedMobs[i].getSpeed()) {
                    ITakeTurns mob = unsortedMobs[i + 1];
                    unsortedMobs[i + 1] = unsortedMobs[i];
                    unsortedMobs[i] = mob;

                    sorted = false;
                }
            }
        } while (!sorted);

        // Now sort them and the player into the mobs arraylist
        boolean playerInserted = false;
        mobs = new ArrayList<>();
        for (ITakeTurns mob : unsortedMobs) {
            mob.setAP(mob.getMaxAP());
            if (mob.getSpeed() > entityManager.getPlayer().getSpeed() || playerInserted) {
                mobs.add(mob);
            } else {
                mobs.add(entityManager.getPlayer());
                playerInserted = true;
                mobs.add(mob);
            }
        }

        if (!playerInserted) {
            mobs.add(entityManager.getPlayer());
        }

        turnMobID = 0;
        System.out.println("Turn initialised");
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (!entityManager.getGame().isBattle()) {
            // End combat
            System.out.println("Not combat");
        }
        // Find who's turn it is
        ITakeTurns currentMob;
        do {
            if (turnMobID == -1 || turnMobID == mobs.size()) {
                // We have reached the end
                initialiseTurn();
            }

            currentMob = mobs.get(turnMobID);
            if (currentMob.getAP() == 0) {
                // Turn over
                System.out.println("0 AP");
                turnMobID++;
            }
        } while (currentMob.getAP() == 0);

        currentMob.update(gc, entityManager.getGame(), delta);
    }
}
