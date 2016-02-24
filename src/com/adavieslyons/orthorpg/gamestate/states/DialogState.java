package com.adavieslyons.orthorpg.gamestate.states;

import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.State;
import com.adavieslyons.orthorpg.gui.DialogGUI;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.IDialogable;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * Created by Ashley on 10/01/2015.
 */
public class DialogState extends State {
    private DialogNode[] dialog;
    private IDialogable parent;
    private final DialogGUI dialogGUI;
    private GameState game;
    private boolean loaded = false;

    public DialogState(GameStateManager gsm, DialogGUI dialogGUI) {
        super(gsm);
        this.dialogGUI = dialogGUI;
    }

    @Override
    public void load(GameContainer gc) throws SlickException {
        loaded = false;
    }

    @Override
    public void unload() {
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (!loaded) {
            loaded = true;
            dialogGUI.setDialog(dialog, parent);
            dialogGUI.loadDialog(gc);
            dialogGUI.beginDialog(gc);
        }

        dialogGUI.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        dialogGUI.render(gc, graphics);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) throws SlickException {

    }

    @Override
    public void keyPressed(int key, char c) {
        dialogGUI.keyPressed(key, c);
    }

    public void setParent(IDialogable parent) {
        this.parent = parent;
    }

    public void setDialog(DialogNode[] dialog) {
        this.dialog = dialog;
    }

    public void setGame(GameState game) {
        this.game = game;
    }
}
