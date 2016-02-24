package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;

/**
 * Created by Ashley on 07/09/2015.
 */
public interface ITakeTurns {
    void starTurn();

    void endTurn();

    boolean isMyTurn();

    int getSpeed();

    int getAP();

    void setAP(int AP);

    void update(GameContainer gc, GameState game, int delta);

    int getMaxAP();
}
