package com.sadwhalestudios.orthorpg;

import com.sadwhalestudios.orthorpg.entities.*;
import com.sadwhalestudios.util.SaveData;
import com.sadwhalestudios.util.map.Map;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;

/**
 *
 * @author Ashley
 */
public class Game extends BasicGame {
    private static Game Instance;
    
    Map map;
    private Input input;
    Player player;
    private SaveData currentGameData;
    
    public static void main(String[] args) throws SlickException
    {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(app.getScreenWidth(), app.getScreenHeight(), true);
        app.start();
    }
    
    private Game()
    {
        super("Orthographic RPG Test");
        Instance = this;
    }
    
    @Override
    public void init(GameContainer gc) throws SlickException
    {
        currentGameData = new SaveData();
        currentGameData.setIntSaveData(0, 50);
        
        input = new Input(gc.getHeight());
        player = new Player();
        System.out.println("Initialising map");
        map = new Map();
        map.load(gc);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
            System.exit(0);
        
        player.update(gc, delta);
        map.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        map.render(gc, graphics);
        player.render(gc, graphics);
    }
    
    /**
     * @return the Instance
     */
    public static Game getInstance() {
        return Instance;
    }

    /**
     * @return the currentGameData
     */
    public SaveData getCurrentGameData() {
        return currentGameData;
    }

    /**
     * @return the input
     */
    public Input getInput() {
        return input;
    }
}
