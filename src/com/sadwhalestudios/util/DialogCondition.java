package com.sadwhalestudios.util;

/**
 *
 * @author Ashley
 */
public class DialogCondition {
    final int id;
    private final String condition;
    private final String[] args;
    
    public DialogCondition(int id, String condition, String args)
    {
        this.id = id;
        this.condition = condition;
        if (!"".equals(args))
            this.args = args.split(",");
        else
            this.args = new String[0];
    }
    
    public boolean conditionMet()
    {
        
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
