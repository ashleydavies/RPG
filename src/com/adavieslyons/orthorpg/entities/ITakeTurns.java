package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 * Created by Ashley on 07/09/2015.
 */
public interface ITakeTurns {
    public void starTurn();
    public void endTurn();
    public boolean isMyTurn();
    public int getSpeed();
    public int getAP();
    public void setAP(int AP);
    public void update(GameContainer gc, GameState game, int delta) throws SlickException;
    public int getMaxAP();
}
