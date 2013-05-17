package com.sadwhalestudios.util;

/**
 *
 * @author
 * Ashley
 */
public class DialogReply {
    final int id;
    final String prompt;
    
    final DialogAction[] actions;
    
    public DialogReply(int id, String prompt, DialogAction[] actions)
    {
        this.id = id;
        this.prompt = prompt;
        this.actions = actions;
    }
    
    @Override
    public String toString()
    {
        String retString = prompt + "\n        =ACTIONS= (" + actions.length + "):\n          ";
        
        for (DialogAction dA: actions)
            retString += "[" + dA + "],";
        
        return retString;
    }
}
