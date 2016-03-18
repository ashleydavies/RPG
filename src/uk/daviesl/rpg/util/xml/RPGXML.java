package uk.daviesl.rpg.util.xml;

import uk.daviesl.rpg.entities.Mob;
import uk.daviesl.rpg.gamestate.states.GameState;
import uk.daviesl.rpg.util.FileLoader;
import uk.daviesl.rpg.util.SpriteSheet;
import uk.daviesl.rpg.util.Vector2i;
import uk.daviesl.rpg.util.dialog.DialogNode;
import uk.daviesl.rpg.util.dialog.DialogReply;
import uk.daviesl.rpg.util.map.GameMap;
import uk.daviesl.rpg.util.map.Map;
import uk.daviesl.rpg.util.map.MapLayer;
import uk.daviesl.rpg.util.map.MapTileData;
import uk.daviesl.rpg.util.dialog.DialogAction;
import uk.daviesl.rpg.util.dialog.DialogCondition;
import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Ashley on 24/02/2016.
 */
public class RPGXML {
    private RPGXML() {
    }


    /* Static helpers */
    private static String substituteDialogString(String stringIn) {
        Enumeration<?> e = XMLParser.ChatSubstitution.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            stringIn = stringIn.replace("[" + key + "]",
                    XMLParser.ChatSubstitution.getProperty(key));
        }

        return stringIn;
    }

    //
    // Loads a texture from a <texture> node
    //
    public static Image loadTexture(Element node) {
        int spriteSheet = firstElemInt(node, "spritesheet");
        int xPos = firstElemInt(node, "xPos");
        int yPos = firstElemInt(node, "yPos");
        return SpriteSheet.getSubImage(spriteSheet, xPos, yPos, 32, 64);
    }

    //
    // Constructs a MobData from a mobID
    //
    public static MobData loadMob(int mobID) {
        MobData data = new MobData();

        Document mobDoc = FileLoader.getXML("mob/" + mobID);
        Element mobInfo = elem(firstElem(mobDoc, "info"));

        Element textureElement = elem(firstElem(mobDoc, "texture"));

        data.setName(strAttr(mobInfo, "name"));
        data.setDialogImage(FileLoader.getImage("ui/avatar/mob/" + strAttr(mobInfo, "avatar")));
        data.setImage(RPGXML.loadTexture(textureElement));

        // Parse dialog if they have any
        NodeList dialogList = mobDoc.getElementsByTagName("dialog");

        if (dialogList.getLength() > 0) {
            data.setDialog(RPGXML.loadDialog(elem(firstElem(mobDoc, "dialog"))));
        }

        return data;
    }

    public static MapData loadMap(GameState game, Document node) {
        // Pre: Map has at least one layer, row and column
        MapData data = new MapData();

        Element layerRoot = elem(firstElem(node, "tileData"));
        NodeList layerNodes = childElems(layerRoot, "layer");

        MapLayer[] layers = new MapLayer[layerNodes.getLength()];

        {
            NodeList firstRow = childElems(elem(layerNodes.item(0)), "row");
            data.setHeight(firstRow.getLength());
            data.setWidth(childElems(elem(firstRow.item(0)), "tile").getLength());
        }

        for (int i = 0; i < layerNodes.getLength(); i++) {
            layers[i] = loadMapLayer(data, elem(layerNodes.item(i)));
        }


        data.setLayers(layers);

        return data;
    }

    private static List<Mob> loadMapMobs(GameState game, GameMap map, Document node) {
        Element mobRoot = (Element) node.getElementsByTagName("mobs").item(0);
        List<Mob> mobs = new ArrayList<Mob>();
        if (mobRoot != null) {
            NodeList mobNodes = mobRoot.getElementsByTagName("mob");

            for (int i = 0; i < mobNodes.getLength(); i++) {
                mobs.add(loadMapMobData(game, elem(mobNodes.item(i))));
            }
        }

        return mobs;
    }

    //
    // Loads map/mob data from a <mob> node
    //
    private static Mob loadMapMobData(GameState game, Element node, GameMap map) {
        int nodeID = intAttr(node, "id");
        int nodeX = intAttr(node, "xPos");
        int nodeY = intAttr(node, "yPos");

        Mob mob = new Mob(game, nodeID, map);

        if (childElems(node, "path").getLength() > 0) {
            Element pathContainer = elem(firstElem(node, "path"));

            NodeList pathNodes = pathContainer
                    .getElementsByTagName("node");

            Vector2i[] path = new Vector2i[pathNodes.getLength()];

            for (int p = 0; p < pathNodes.getLength(); p++) {
                Element pathNode = (Element) pathNodes.item(p);
                path[p] = new Vector2i(intAttr(pathNode, "xPos"), intAttr(pathNode, "yPos"));
            }
            mob.setPath(path);
        }
        mob.setPosition(new Vector2i(nodeX, nodeY));

        return mob;
    }

    //
    // Loads mob path data from a <path> node
    //


    //
    // Loads map layers from a map document's layer nodes
    //
    private static MapLayer loadMapLayer(MapData data, Element node) {
        NodeList rowNodes = childElems(node, "row");

        MapTileData[][] layerTiles = new MapTileData[data.getHeight()][data.getWidth()];

        for (int i = 0; i < data.getHeight(); i++) {
            layerTiles[i] = loadMapRow(data, elem(rowNodes.item(i)));
        }

        return new MapLayer(layerTiles);
    }

    //
    // Loads a map row from a <row> node
    //
    private static MapTileData[] loadMapRow(MapData data, Element node) {
        NodeList colNodes = childElems(node, "tile");

        MapTileData[] rowTiles = new MapTileData[data.getWidth()];

        for (int i = 0; i < data.getWidth(); i++) {
            rowTiles[i] = loadMapTile(elem(colNodes.item(i)));
        }

        return rowTiles;
    }

    //
    // Loads a map tile from a <tile> node
    //
    private static MapTileData loadMapTile(Element node) {
        return new MapTileData(intAttr(node, "tileID"));
    }

    //
    // Loads dialog from a <dialog> node
    //
    public static DialogNode[] loadDialog(Element node) {
        NodeList dialogNodes = childElems(node, "node");
        NodeList conditionNodes = childElems(node, "condition");

        DialogNode[] nodeDialogs = new DialogNode[dialogNodes.getLength()];
        DialogCondition[] conditions = new DialogCondition[conditionNodes.getLength()];

        for (int i = 0; i < conditionNodes.getLength(); i++) {
            conditions[i] = loadDialogCondition(elem(conditionNodes.item(i)));
        }

        for (int i = 0; i < dialogNodes.getLength(); i++) {
            nodeDialogs[i] = loadDialogNode(elem(dialogNodes.item(i)), conditions);
        }

        return nodeDialogs;
    }

    //
    // Loads a condition from a <condition> node
    //
    private static DialogCondition loadDialogCondition(Element node) {
        int nodeID = intAttr(node, "id");
        String nodeCondition = strAttr(node, "condition");
        String nodeArguments = strAttr(node, "args");

        return new DialogCondition(nodeID, nodeCondition, nodeArguments);
    }

    //
    // Loads a dialog node from a <node> node
    //
    private static DialogNode loadDialogNode(Element node, DialogCondition[] conditions) {
        int nodeID = intAttr(node, "id");
        String nodePrompt = strAttr(node, "prompt");

        NodeList replyNodes = childElems(node, "reply");
        DialogReply[] nodeReplies = new DialogReply[replyNodes.getLength()];

        for (int i = 0; i < replyNodes.getLength(); i++) {
            nodeReplies[i] = loadReplyNode(elem(replyNodes.item(i)), conditions);
        }

        return new DialogNode(nodeID, nodePrompt, nodeReplies);
    }

    //
    // Loads a dialog reply from a <reply> node
    //
    private static DialogReply loadReplyNode(Element node, DialogCondition[] conditions) {
        int nodeID = intAttr(node, "id");
        String nodePrompt = substituteDialogString(strAttr(node, "prompt"));

        NodeList actionNodes = childElems(node, "action");
        NodeList conditionNodes = childElems(node, "replyCondition");

        DialogAction[] nodeActions = new DialogAction[actionNodes.getLength()];
        DialogCondition[] nodeConditions = new DialogCondition[conditionNodes.getLength()];

        for (int i = 0; i < actionNodes.getLength(); i++) {
            nodeActions[i] = loadDialogAction(elem(actionNodes.item(i)), conditions);
        }

        for (int i = 0; i < conditionNodes.getLength(); i++) {
            nodeConditions[i] = getCondition(elem(conditionNodes.item(i)), conditions);
        }

        return new DialogReply(nodeID, nodePrompt, nodeActions, nodeConditions);
    }

    //
    // Loads a dialog action from an <action> node
    //
    private static DialogAction loadDialogAction(Element node, DialogCondition[] conditions) {
        int nodeID = intAttr(node, "id");
        String nodeAction = strAttr(node, "action");
        String nodeArguments = strAttr(node, "args");

        NodeList conditionNodes = childElems(node, "actionCondition");
        DialogCondition[] nodeConditions = new DialogCondition[conditionNodes.getLength()];

        for (int i = 0; i < conditionNodes.getLength(); i++) {
            nodeConditions[i] = getCondition(elem(conditionNodes.item(i)), conditions);
        }

        return new DialogAction(nodeID, nodeAction, nodeArguments, nodeConditions);
    }

    //
    // Gets a dialog condition from a reply/action condition link and a list of conditions to take from
    //
    private static DialogCondition getCondition(Element node, DialogCondition[] conditions) {
        int conditionID = intAttr(node, "conditionID");
        return conditions[conditionID];
    }

    //
    // XML DOM helper functions
    // Ideally these would be mostly in an extended Element class, however cannot be extended as it's an interface
    //   for a hierarchy of elements.
    //
    private static Element elem(Node node) {
        return (Element) node;
    }

    private static int intAttr(Element node, String attr) {
        return Integer.parseInt(node.getAttribute(attr));
    }

    private static String strAttr(Element node, String attr) {
        return node.getAttribute(attr);
    }

    private static int firstElemInt(Element node, String child) {
        return Integer.parseInt(firstElemText(node, child));
    }

    private static String firstElemText(Element node, String child) {
        return firstElem(node, child).getTextContent();
    }

    private static NodeList childElems(Element node, String child) {
        return node.getElementsByTagName(child);
    }

    private static NodeList childElems(Document node, String child) {
        return node.getElementsByTagName(child);
    }

    private static Node firstElem(Element node, String child) {
        return childElems(node, child).item(0);
    }

    private static Node firstElem(Document node, String child) {
        return childElems(node, child).item(0);
    }
}
