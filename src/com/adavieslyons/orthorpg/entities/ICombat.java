package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 * Created by Ashley on 12/01/2015.
 */
public interface ICombat {
    public int getFortitude();
    public int getStrength();
    public int getIntelligence();
    public int getSwordsmanship();
    public int getArchery();
    public int getSpeed();
    public int getCombatMagic();
    public int getDarkMagic();
    public int getUtilityMagic();
    public int getHealingMagic();
    public int getProtectiveMagic();

    public void update(GameContainer gc, GameState game, int delta) throws SlickException;

    public int getMaxAP();
    public void setAP(int AP);
    public int getAP();

    public int getMaxHP();
    public void setHP(int HP);
    public int getHP();

    public int getMaxMana();
    public void setMana(int Mana);
    public int getMana();
}
