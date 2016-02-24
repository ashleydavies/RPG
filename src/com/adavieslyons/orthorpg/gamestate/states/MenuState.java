package com.adavieslyons.orthorpg.gamestate.states;

import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.State;
import com.adavieslyons.util.FileLoader;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;

public class MenuState extends State {
    private Image buttonTexture;
    private Image headerImage;
    private final String[] buttons = {"Play", "Settings", "Help", "Exit"};

    public MenuState(GameStateManager gsm) {
        super(gsm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void load(GameContainer gc) throws SlickException {
        buttonTexture = FileLoader.getImage("button");
        headerImage = FileLoader.getImage("header");
    }

    @Override
    public void unload() {

    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (Keyboard.isKeyDown(Input.KEY_ENTER))
            gameStateManager.setState(gc, new GameState(gameStateManager));
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        graphics.setBackground(Color.darkGray);
        graphics.drawImage(headerImage, gc.getWidth() / 2 - headerImage.getWidth() / 2, 10);

        int bNo = 0;

        for (String button : buttons) {
            graphics.drawImage(buttonTexture, gc.getWidth() / 2 - buttonTexture.getWidth() / 2, 220 + 120 * bNo);
            graphics.setColor(Color.black);
            graphics.drawString(button, gc.getWidth() / 2 - graphics.getFont().getWidth(button) / 2, 255 + 120 * bNo);
            bNo++;
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) throws SlickException {

    }

    @Override
    public void keyPressed(int key, char c) {

    }
}
