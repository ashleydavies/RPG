package com.adavieslyons.util.dialog;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

/**
 * @author Ashley
 */
public class DialogAction {
    final int id;
    private final String action;
    private final String[] args;
    private final DialogCondition[] conditions;

    public DialogAction(int id, String action, String args,
                        DialogCondition[] conditions) {
        this.id = id;
        this.action = action;
        if (!"".equals(args))
            this.args = args.split(",");
        else
            this.args = new String[0];
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        String retString = getAction() + " ARGS: (";

        for (String arg : getArgs())
            retString += arg + ",";

        retString += ") CONDITIONS: (";

        for (DialogCondition cond : conditions)
            retString += cond + ",";

        retString += ")";

        return retString;
    }

    public boolean conditionsMet(GameState game) {
        for (DialogCondition condition : conditions) {
            if (!condition.conditionMet(game)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * @return an arg
     */
    public String getArg(int i) {
        return args[i];
    }

    /**
     * @return the conditions
     */
    public DialogCondition[] getConditions() {
        return conditions;
    }
}
