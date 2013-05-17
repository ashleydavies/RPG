package com.sadwhalestudios.util;

/**
 *
 * @author
 * Ashley
 */
public class DialogAction {
    final int id;
    final String action;
    final String[] args;
    
    public DialogAction(int id, String action, String args)
    {
        this.id = id;
        this.action = action;
        if (args != "")
            this.args = args.split(",");
        else
            this.args = new String[0];
    }
    
    @Override
    public String toString()
    {
        String retString = action + " (";
        
        for (String arg: args)
            retString += arg + ",";
        
        retString += ")";
        
        return retString;
    }
}
