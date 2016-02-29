package uk.daviesl.rpg.util.dialog;

import uk.daviesl.rpg.gamestate.states.GameState;

/**
 * @author Ashley
 */
public class DialogNode {
    private final int id;
    private final String prompt;
    private final DialogReply[] replies;

    public DialogNode(int id, String prompt, DialogReply[] replies) {
        this.id = id;
        this.prompt = prompt;
        this.replies = replies;
    }

    @Override
    public String toString() {
        String retString = "CHAT NODE " + id + ":\n   " + getPrompt()
                + "\n   =REPLIES= (" + getReplies().length + ")\n     ";

        for (DialogReply dR : getReplies())
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

    /**
     * @return a reply
     */
    public DialogReply getReply(int i) {
        return replies[i];
    }

    /**
     * @return a reply based on whether conditions are met (Ie n would be the
     * n-1th reply that has conditions met. Basically gets the i-th
     * reply while filtering ones that don't meet the conditions.
     */
    public DialogReply getReplyCM(GameState game, int i) {
        int k = 0;

        for (DialogReply reply : replies)
            if (reply.conditionsMet(game))
                if (k == i)
                    return reply;
                else
                    k++;

        return null;
    }

    public String[] getReplyPrompts(GameState game) {
        // Only where conditions met
        int count = 0;

        for (DialogReply reply : replies)
            if (reply.conditionsMet(game))
                count++;

        String[] retString = new String[count];
        int i = 0;
        for (DialogReply reply : replies)
            if (reply.conditionsMet(game))
                retString[i++] = reply.getPrompt();

        return retString;
    }
}
