package com.sadwhalestudios.orthorpg;

import com.sadwhalestudios.orthorpg.entities.*;
import com.sadwhalestudios.util.FileUtility;
//import com.sadwhalestudios.gui.Button;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.tiled.*;

/**
 *
 * @author Ashley
 */
public class Game extends BasicGame {
    TiledMap map;
    Player player;
    NPC farmer_joe;
    
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(1920, 1080, true);
        app.start();
    }
    
    public Game() {
        super("Orthographic RPG Test");
    }
    
    @Override
    public void init(GameContainer gc) throws SlickException {
        map = new TiledMap("resources/map/map.tmx");
        player = new Player();
        farmer_joe = new NPC(1);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
            System.exit(0);
        
        player.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        map.render(0, 0);
        
        player.render(gc, graphics);
    }
}
