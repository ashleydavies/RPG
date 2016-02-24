package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 * Created by Ashley on 12/01/2015.
 */
public interface ICombat {
    int getFortitude();

    int getStrength();

    int getIntelligence();

    int getSwordsmanship();

    int getArchery();

    int getSpeed();

    int getCombatMagic();

    int getDarkMagic();

    int getUtilityMagic();

    int getHealingMagic();

    int getProtectiveMagic();

    void update(GameContainer gc, GameState game, int delta);

    void attack(ICombat enemy);

    int getMaxAP();

    void setAP(int AP);

    int getAP();

    int getMaxHP();

    void setHP(int HP);

    int getHP();

    int getMaxMana();

    void setMana(int Mana);

    int getMana();
}
