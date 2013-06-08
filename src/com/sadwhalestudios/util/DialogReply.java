package com.sadwhalestudios.util;

/**
 *
 * @author
 * Ashley
 */
public class DialogReply {
    final int id;
    private final String prompt;
    
    private final DialogAction[] actions;
    //private final DialogCondition[] conditions;
    
    public DialogReply(int id, String prompt, DialogAction[] actions)//, DialogCondition[] conditions)
    {
        this.id = id;
        this.prompt = prompt;
        this.actions = actions;
        //this.conditions = conditions;
    }
    
    @Override
    public String toString()
    {
        String retString = getPrompt() + "\n        =ACTIONS= (" + getActions().length + "):\n          ";
        
        for (DialogAction dA: getActions())
            retString += "[" + dA + "],";
        
        return retString;
    }

    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @return the actions
     */
    public DialogAction[] getActions() {
        return actions;
    }

    /**
     * @return the conditions
     *
    public DialogCondition[] getConditions() {
        return conditions;
    }*/
}
