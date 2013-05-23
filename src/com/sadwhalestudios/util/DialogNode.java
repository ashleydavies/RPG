package com.sadwhalestudios.util;

/**
 *
 * @author
 * Ashley
 */
public class DialogNode {
    final int id;
    private final String prompt;
    private final DialogReply[] replies;
    
    public DialogNode(int id, String prompt, DialogReply[] replies)
    {
        this.id = id;
        this.prompt = prompt;
        this.replies = replies;
    }
    
    @Override
    public String toString()
    {
        String retString = "CHAT NODE " + id + ":\n   " + getPrompt() + "\n   =REPLIES= (" + getReplies().length + ")\n     ";
        
        for (DialogReply dR: getReplies())
            retString += dR + "\n     ";
        
        return retString;
    }

    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @return the replies
     */
    public DialogReply[] getReplies() {
        return replies;
    }

    public String[] getReplyPrompts() {
        String[] retString = new String[replies.length];
        int i = 0;
        for (DialogReply reply: replies)
            retString[i++] = reply.getPrompt();
        
        return retString;
    }
}
