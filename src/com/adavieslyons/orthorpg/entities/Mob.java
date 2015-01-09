package com.adavieslyons.orthorpg.entities;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.DialogGUI;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.XMLParser;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.IDialogable;
import com.adavieslyons.util.map.Map;
import com.adavieslyons.util.map.MapTileData;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Ashley
 */
public class Mob extends MovingPathEntity implements IDialogable, IAttackable {
    private String name;
    private DialogNode[] dialog;
    private MapTileData tileOccupied;
    private Image dialogImage;

    private DialogGUI dialogGUI;

    public Mob(GameContainer gc, GameState game, int mobID, Map map,
               Vector2i path[]) throws SlickException {
        super(map);

        setPath(path);

        loadDataFromXML(gc, mobID, game);
    }

    public void loadDataFromXML(GameContainer gc, int mobID, GameState game)
            throws SlickException {
        Document mobDoc = XMLParser.instance.parseXML(this.getClass()
                .getClassLoader()
                .getResourceAsStream("data/xml/mob/" + mobID + ".xml"));

        Element mobInfo = (Element) mobDoc.getElementsByTagName("info").item(0);

        String imageName = mobInfo.getAttribute("avatar");
        Element textureElement = (Element) mobDoc.getElementsByTagName(
                "texture").item(0);

        name = mobInfo.getAttribute("name");
        dialogImage = new Image("img/ui/avatar/mob/" + imageName);
        setImage(XMLParser.loadTexture(textureElement));

        // Parse dialog if they have any
        NodeList dialogList = mobDoc.getElementsByTagName("dialog");

        if (dialogList.getLength() > 0) {
            dialog = XMLParser.loadDialog((Element) dialogList.item(0));
        }


    }

    @Override
    public void update(GameContainer gc, GameState game, int delta)
            throws SlickException {
        updateMove(delta);
        updatePath();
    }

    @Override
    public void render(GameContainer gc, Graphics graphics)
            throws SlickException {
        super.render(gc, graphics);
    }

    @Override
    public void onClick(GameState game) throws SlickException {
        game.showDialog(dialog, this);
    }


    @Override
    public void dialogCloseRequested() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void occupiedTileStartChange(Vector2i newTile) {
        tileOccupied = map.setOccupied(newTile.getX(), newTile.getY(), this);
    }

    @Override
    protected void occupiedTileEndChange(Vector2i oldTile) {
        map.setOccupied(oldTile.getX(), oldTile.getY(), null);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDialogTitle() {
        return name;
    }

    @Override
    public Image getDialogImage() {
        return dialogImage;
    }
}
