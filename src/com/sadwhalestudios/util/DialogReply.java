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
    
    public DialogReply(int id, String prompt, DialogAction[] actions)
    {
        this.id = id;
        this.prompt = prompt;
        this.actions = actions;
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
}
