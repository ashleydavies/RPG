package uk.daviesl.rpg;

import uk.daviesl.rpg.gamestate.GameStateManager;
import uk.daviesl.rpg.gamestate.states.MenuState;
import org.newdawn.slick.*;

/**
 * @author Ashley
 */
public class Game extends BasicGame {
    public static final int TILE_SIZE_X = 64;
    public static final int TILE_SIZE_Y = 32;

    private GameStateManager gsm;

    private Game() {
        super("RPG");
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(1200, 800, false);
        app.setShowFPS(false);
        app.setTargetFrameRate(60);
        app.start();
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gsm = new GameStateManager();
        //GameState s = new GameState(gsm);
        MenuState s = new MenuState(gsm);
        gsm.pushState(gc, s);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        gsm.mouseClicked(button, x, y, clickCount);
    }

    @Override
    public void keyPressed(int key, char c) {
        gsm.keyPressed(key, c);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        gsm.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        gsm.render(gc, graphics);
    }

}
