package com.adavieslyons.orthorpg.gamestate.states;

import com.adavieslyons.orthorpg.entities.Entity;
import com.adavieslyons.orthorpg.entities.EntityManager;
import com.adavieslyons.orthorpg.entities.Player;
import com.adavieslyons.orthorpg.gamestate.GameStateManager;
import com.adavieslyons.orthorpg.gamestate.State;
import com.adavieslyons.orthorpg.gui.DialogGUI;
import com.adavieslyons.orthorpg.gui.InventoryGUI;
import com.adavieslyons.util.SaveData;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.IDialogable;
import com.adavieslyons.util.inventory.Item;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.WorldMap;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class GameState extends State {
    public int WIDTH;
    public int HEIGHT;
    Map map;
    private WorldMap worldMap;
    private DialogState dialogState;

    private boolean isBattle = false;
    private Input input;
    private Player player;
    private SaveData currentGameData;
    private InventoryGUI inventoryGUI;
    private EntityManager entityManager;
    private InnerState state;

    public GameState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void load(GameContainer gc) throws SlickException {
        WIDTH = gc.getWidth();
        HEIGHT = gc.getHeight();

        currentGameData = new SaveData();
        currentGameData.setIntSaveData(0, 50);

        dialogState = new DialogState(gameStateManager, new DialogGUI(gc, this));
        dialogState.setGame(this);

        input = new Input(gc.getHeight());
        map = new Map();
        entityManager = new EntityManager(map);
        map.load(gc, this, 0, entityManager);

        player = new Player(gc, this, map, new Vector2i(40, 7));
        entityManager.setPlayer(player);

        worldMap = new WorldMap();
        map.focusTile(new Vector2i(40, 7));
        inventoryGUI = new InventoryGUI(gc, this);

        Item.LoadItems(this);
        loadState(InnerState.PLAYING);


        for (int i = 0; i < 22; i++)
            System.out.println(i + " " + SaveData.getFriendlyName(i));
    }

    public void loadState(InnerState state) {
        this.state = state;
        switch (state) {
            case PLAYING:
            case INVENTORY:
                map.setEditing(false);
                break;
            case MAP_EDITOR:
                map.setEditing(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void unload() {

    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        WIDTH = gc.getWidth();
        HEIGHT = gc.getHeight();

        switch (state) {
            case PLAYING:
                if (Keyboard.isKeyDown(Input.KEY_ESCAPE))
                    System.exit(0);

                if (Keyboard.isKeyDown(Input.KEY_E)) {
                    state = InnerState.INVENTORY;
                    break;
                }

                if (Keyboard.isKeyDown(Input.KEY_1)
                        && Keyboard.isKeyDown(Input.KEY_Z)) {
                    loadState(InnerState.MAP_EDITOR);
                    break;
                }
                entityManager.update(gc, this, delta);
                player.update(gc, this, delta);
                map.update(gc, this, delta);

                if (player.getPosition().getY() == 0) {
                    System.out.println("Player transferring map (Via North)");
                    loadState(InnerState.WORLD_MAP);
                    worldMap.leavingMapArea(map.getID(), WorldMap.MapDirection.NORTH);
                } else if (player.getPosition().getX() == map.getWidth() - 1) {
                    System.out.println("Player transferring map (Via East)");
                    loadState(InnerState.WORLD_MAP);
                    worldMap.leavingMapArea(map.getID(), WorldMap.MapDirection.EAST);
                } else if (player.getPosition().getY() == map.getHeight() - 1) {
                    System.out.println("Player transferring map (Via South)");
                    loadState(InnerState.WORLD_MAP);
                    worldMap.leavingMapArea(map.getID(), WorldMap.MapDirection.SOUTH);
                } else if (player.getPosition().getX() == 0) {
                    System.out.println("Player transferring map (Via West)");
                    loadState(InnerState.WORLD_MAP);
                    worldMap.leavingMapArea(map.getID(), WorldMap.MapDirection.WEST);
                }
                break;
            case WORLD_MAP:
                worldMap.update(gc, this, delta);
                break;
            case INVENTORY:
                if (input.isKeyPressed(Input.KEY_A))
                    System.out.println("Hello");
                inventoryGUI.update(gc, delta);
            case MAP_EDITOR:
                map.update(gc, this, delta);
                if (input.isKeyDown(Input.KEY_X))
                    map.exportXML();
            default:
                break;
        }
    }

    public boolean isBattle() {
        return isBattle;
    }

    public void setBattle(boolean isBattle) {
        this.isBattle = isBattle;
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        switch (state) {
            case PLAYING:
            case INVENTORY:
                map.render(gc, graphics);
                map.renderPostEntities(gc, graphics);
                if (state == InnerState.INVENTORY)
                    inventoryGUI.render(gc, graphics);
                break;
            case WORLD_MAP:
                worldMap.render(gc, graphics);
                break;
            case MAP_EDITOR:
                map.render(gc, graphics);
                break;
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) throws SlickException {
        Entity entity = entityManager.getMobFromScreenCoordinates(x, y);

        if (entity != null) {
            entity.onClick(this);
            return;
        }

        // No entity was clicked, notify player
        player.gameClicked(x, y);
    }

    @Override
    public void keyPressed(int key, char c) throws SlickException {

    }

    public void loadMap(GameContainer gc, int mapID, WorldMap.MapDirection direction) throws SlickException {
        map = new Map();
        System.out.println("Loading Map " + mapID);
        entityManager.clear();
        map.load(gc, this, mapID, entityManager);
        Vector2i playerPosition = map.getSuitablePlayerLocation(direction);
        player.onNewMapLoad(map, playerPosition);
        map.focusTile(playerPosition);
        loadState(InnerState.PLAYING);
    }
    //GameContainer gc;
    public SaveData getCurrentGameData() {
        return currentGameData;
    }

    public Input getInput() {
        return input;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void showDialog(DialogNode[] dialog, IDialogable parent) throws SlickException {
        dialogState.setDialog(dialog);
        dialogState.setParent(parent);
        gameStateManager.awaitTickPushState(dialogState);
    }

    private enum InnerState {
        PLAYING, WORLD_MAP, INVENTORY, MAP_EDITOR
    }
}
