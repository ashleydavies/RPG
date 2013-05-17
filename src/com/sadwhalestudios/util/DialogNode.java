package com.sadwhalestudios.util;

/**
 *
 * @author
 * Ashley
 */
public class DialogNode {
    final int id;
    final String prompt;
    
    final DialogReply[] replies;
    
    public DialogNode(int id, String prompt, DialogReply[] replies)
    {
        this.id = id;
        this.prompt = prompt;
        this.replies = replies;
    }
    
    @Override
    public String toString()
    {
        String retString = "CHAT NODE " + id + ":\n   " + prompt + "\n   =REPLIES= (" + replies.length + ")\n     ";
        
        for (DialogReply dR: replies)
            retString += dR + "\n     ";
        
        return retString;
    }
}
