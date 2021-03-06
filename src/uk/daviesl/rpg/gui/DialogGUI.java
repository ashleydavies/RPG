package uk.daviesl.rpg.gui;

import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.util.SaveData;
import uk.daviesl.rpg.util.dialog.DialogAction;
import uk.daviesl.rpg.util.dialog.DialogNode;
import uk.daviesl.rpg.util.dialog.DialogReply;
import uk.daviesl.rpg.util.dialog.IDialogable;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.MouseOverArea;

import java.awt.Font;

/**
 * @author Ashley
 */
public final class DialogGUI extends GUIWindow {
    private static final TrueTypeFont dialogFont;
    private static final TrueTypeFont dialogTitleFont;

    private boolean[] replyAreasMouseDown;
    private MouseOverArea[] replyAreas;
    private DialogNode[] dialog;
    private int currentDialog;
    private int typedNum = -1;
    private IDialogable parent;

    static {
        //ui = FileLoader.getImage("ui/ui");
        dialogFont = new TrueTypeFont(
                new Font("sans-serif", Font.PLAIN, 17), true);
        dialogTitleFont = new TrueTypeFont(
                new Font("Arial", Font.BOLD, 36), true);
    }

    public DialogGUI(GameContainer gc, GameState game) throws SlickException {
        super(gc, game, 504, 624);
    }

    public void loadDialog(GameContainer gc) throws SlickException {
        String content = dialog[0].getPrompt();
        String[] responses = dialog[0].getReplyPrompts(game);

        System.out.println("LOADING DIALOG");
        renderPrimaryContent(gc, content, responses);
    }

    public void setDialog(DialogNode[] dialog, IDialogable parent) {
        this.dialog = dialog;
        this.parent = parent;
    }

    public void keyPressed(int key, char c) {
        if (key >= 2 && key <= 10)
            typedNum = key - 2; // Key codes for 1-9 are 2-10
    }

    private void renderPrimaryContent(GameContainer gc, String content,
                                      String[] responses) throws SlickException {
        content = prepareString(content);
        responses = prepareStrings(responses);

        // TODO: This whole class needs to be redone but this part especially
        replyAreas = new MouseOverArea[responses.length];
        replyAreasMouseDown = new boolean[responses.length];

        Graphics graphics = gc.getGraphics();
        graphics.clear();
        graphics.setBackground(new Color(0, 0, 0, 0));

        int y = 12 + 32;

        graphics.setColor(Color.black);
        graphics.setFont(dialogTitleFont);
        graphics.drawString(parent.getDialogTitle(), 12 + 64, 16);

        graphics.setColor(Color.black);
        graphics.setFont(dialogFont);
        for (String line : content.split("\n"))
            graphics.drawString(line, 12 + 64, y += 18);

        graphics.setColor(Color.blue);
        y += 36;

        int replyN = 0;

        for (String reply : responses) {
            int lineN = 0;
            for (String line : reply.split("\n")) {
                if (lineN == 0)
                    graphics.drawString(++replyN + ": " + line,
                            12 + 64 - dialogFont.getWidth(replyN + ": "),
                            y += 22);
                else
                    graphics.drawString(line, 12 + 64, y += 22);

                lineN++;
            }

            replyAreas[replyN - 1] = new MouseOverArea(gc, new Image(
                    (int) (windowRect.getWidth() - 24 - 64), 18 * lineN),
                    (int) (windowRect.getX() + 12 + 64),
                    (int) (windowRect.getY() + y - ((lineN - 1) * 18)));
            replyAreasMouseDown[replyN - 1] = false;
        }
        graphics.drawImage(parent.getDialogImage(), BW + 8, BW + 8,
                BW + 8 + 56, BW + 8 + 120, 0, 0, 64, 128);
        graphics.copyArea(windowDynamicContent, 0, 0);
        graphics.clear();
    }

    private String[] prepareStrings(String[] content) {
        String[] retArray = new String[content.length];
        int i = 0;
        for (String str : content)
            retArray[i++] = prepareString(str);

        return retArray;
    }

    private String prepareString(String content) {
        String prepString = " ";
        content = content.replace("[N]", "\n");
        String[] content_words = content.split(" ");

        int curLine = 1;

        for (String word : content_words) {
            if ("\n".equals(word)) {
                prepString += "\n";
                curLine = 0;
                continue;
            }

            curLine += dialogFont.getWidth(word);

            if (curLine > windowRect.getWidth() - 42 - 64 - 42) {
                curLine = dialogFont.getWidth(word);
                prepString += "\n";
            }
            prepString += word + " ";
        }

        return prepString;
    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {
        if (typedNum != -1) {
            dialogReplyClicked(gc, typedNum);
            renderDefaultContent(gc);
            typedNum = -1;
        }

        for (int i = 0; i < replyAreas.length; i++) {
            if (replyAreas[i].isMouseOver() && Mouse.isButtonDown(0)) {
                replyAreasMouseDown[i] = true;
            } else if (replyAreas[i].isMouseOver() && !Mouse.isButtonDown(0)
                    && replyAreasMouseDown[i]) {
                replyAreasMouseDown[i] = false;

                System.out.println("Clicked " + i);
                // parent.dialogReplyClicked(gc, game, i);
                dialogReplyClicked(gc, i);

                renderDefaultContent(gc);

                break;
            }

            if (!replyAreas[i].isMouseOver()) {
                replyAreasMouseDown[i] = false;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        graphics.drawImage(windowBg, windowRect.getX(), windowRect.getY());
        graphics.drawImage(windowDefaultContent, windowRect.getX(),
                windowRect.getY());
        graphics.drawImage(windowDynamicContent, windowRect.getX(),
                windowRect.getY());
    }

    private void dialogReplyClicked(GameContainer gc, int i)
            throws SlickException {
        // i = reply clicked
        DialogReply reply;
        try {
            reply = dialog[currentDialog].getReplyCM(game, i);
        } catch (NullPointerException e) {
            return; // Happens if i is out of bounds of replies usually
        }
        System.out.println(reply);
        for (DialogAction action : reply.getActions()) {
            System.out.println("ACTION: " + action);

            if (action.conditionsMet(game)) {
                SaveData data = game.getCurrentGameData();
                switch (action.getAction()) {
                    case "changeNode": {
                        currentDialog = Integer.parseInt(action.getArg(0));
                        renderPrimaryContent(gc,
                                dialog[currentDialog].getPrompt(),
                                dialog[currentDialog].getReplyPrompts(game));
                        break;
                    }
                    case "intdata_decrease": {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, data.getIntSaveData(iData)
                                - modif);
                        break;
                    }
                    case "intdata_increase": {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, data.getIntSaveData(iData)
                                + modif);
                        break;
                    }
                    case "intdata_set": {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, modif);
                        break;
                    }
                    case "endDialog": {
                        endDialog();
                        break;
                    }
                }
            }
        }
    }

    public void beginDialog(GameContainer gc)
            throws SlickException {
        currentDialog = 0;
        renderPrimaryContent(gc, dialog[0].getPrompt(),
                dialog[0].getReplyPrompts(game));
    }

    private void endDialog() {
        parent.dialogCloseRequested();
    }
}
