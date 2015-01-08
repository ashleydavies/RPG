package com.adavieslyons.util.dialog;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

/**
 * @author Ashley
 */
public class DialogCondition {
    final int id;
    private final String condition;
    private final String[] args;

    public DialogCondition(int id, String condition, String args) {
        this.id = id;
        this.condition = condition;
        if (!"".equals(args))
            this.args = args.split(",");
        else
            this.args = new String[0];
    }

    public boolean conditionMet(GameState game) {
        switch (condition) {
            case "intdata_morethan":
                return game.getCurrentGameData().getIntSaveData(
                        Integer.parseInt(args[0])) > Integer.parseInt(args[1]);
            case "intdata_lessthan":
                return game.getCurrentGameData().getIntSaveData(
                        Integer.parseInt(args[0])) < Integer.parseInt(args[1]);
            case "intdata_equalto":
                return game.getCurrentGameData().getIntSaveData(
                        Integer.parseInt(args[0])) == Integer.parseInt(args[1]);
        }
        return true;
    }

    @Override
    public String toString() {
        String retString = getCondition() + "(";

        for (String arg : getArgs())
            retString += arg + ",";

        retString += ")";

        return retString;
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }
}
