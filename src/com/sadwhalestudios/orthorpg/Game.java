package com.sadwhalestudios.orthorpg;

import com.sadwhalestudios.orthorpg.entities.*;
import com.sadwhalestudios.util.SaveData;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.*;

/**
 *
 * @author Ashley
 */
public class Game extends BasicGame {
    private static Game Instance;
    
    TiledMap map;
    Player player;
    NPC farmer_joe;
    private SaveData currentGameData;
    //DialogGUI dGUI;
    
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
        
        map = new TiledMap("map/map.tmx");
        player = new Player();
        farmer_joe = new NPC(gc, 1);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
            System.exit(0);
        
        player.update(gc, delta);
        farmer_joe.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        map.render(0, 0);
        
        player.render(gc, graphics);
        farmer_joe.render(gc, graphics);
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
}
