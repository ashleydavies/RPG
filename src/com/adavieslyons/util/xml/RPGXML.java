package com.adavieslyons.util.xml;

import com.adavieslyons.util.FileLoader;
import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.dialog.DialogAction;
import com.adavieslyons.util.dialog.DialogCondition;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.DialogReply;
import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Enumeration;

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

        data.name = strAttr(mobInfo, "name");
        data.dialogImage = FileLoader.getImage("ui/avatar/mob/" + strAttr(mobInfo, "avatar"));
        data.image = RPGXML.loadTexture(textureElement);

        // Parse dialog if they have any
        NodeList dialogList = mobDoc.getElementsByTagName("dialog");

        if (dialogList.getLength() > 0) {
            data.dialog = RPGXML.loadDialog(elem(firstElem(mobDoc, "dialog")));
        }

        return data;
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

    private static NodeList childElems(Node node, String child) {
        return childElems(node, child);
    }

    private static Node firstElem(Node node, String child) {
        return childElems(node, child).item(0);
    }
}
