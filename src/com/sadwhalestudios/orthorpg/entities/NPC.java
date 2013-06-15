package com.sadwhalestudios.orthorpg.entities;

import com.sadwhalestudios.orthorpg.Game;
import com.sadwhalestudios.orthorpg.gui.DialogGUI;
import com.sadwhalestudios.util.dialog.DialogAction;
import com.sadwhalestudios.util.dialog.DialogCondition;
import com.sadwhalestudios.util.dialog.DialogNode;
import com.sadwhalestudios.util.dialog.DialogReply;
import com.sadwhalestudios.util.SaveData;
import com.sadwhalestudios.util.XMLParser;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.w3c.dom.*;

/**
 *
 * @author Ashley
 */
public class NPC {
    static final Properties NPCSubstitution;
    
    Document info;
    DialogNode[] dialog;
    DialogGUI dGUI;
    private final String name;
    private final Image avatar;
    boolean dialogShowing = false;
    int curDialog = 0;
    
    static
    {
        NPCSubstitution = new Properties();
        try {
            NPCSubstitution.load(NPC.class.getClassLoader().getResourceAsStream("data/properties/NPCSubstitution.properties"));
        } catch (IOException ex) {
            Logger.getLogger(NPC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public NPC(GameContainer gc, int npcID) throws SlickException
    {        
        info = XMLParser.instance.parseXML(this.getClass().getClassLoader().getResourceAsStream("data/xml/npc/1.xml"));
        
        
        // TODO: Sort out parsing code; preferably shift out of constructor to avoid laggy game loading.
        
        name = info.getElementsByTagName("name").item(0).getTextContent();
        avatar = new Image("img/ui/avatar/npc/" + info.getElementsByTagName("avatar").item(0).getTextContent());
        
        Element dialogRoot = (Element)info.getElementsByTagName("dialog").item(0);
        NodeList dialogNodes = dialogRoot.getElementsByTagName("node");
        
        dialog = new DialogNode[dialogNodes.getLength()];
        
        NodeList i_conditionNodes = dialogRoot.getElementsByTagName("condition");

        DialogCondition[] i_conditions = new DialogCondition[i_conditionNodes.getLength()];

        for (int l = 0; l < i_conditionNodes.getLength(); l++)
        {
            Element i_conditionNode = (Element)i_conditionNodes.item(l);

            int i_conditionNodeID = Integer.parseInt(i_conditionNode.getAttribute("id"));
            String i_conditionNodeCondition = i_conditionNode.getAttribute("condition");
            String i_conditionNodeArguments = i_conditionNode.getAttribute("args");

            i_conditions[i_conditionNodeID] = new DialogCondition(i_conditionNodeID, i_conditionNodeCondition, i_conditionNodeArguments);
        }
        
        for (int i = 0; i < dialogNodes.getLength(); i += 1)
        {
            Element i_dialogNode = (Element)dialogNodes.item(i);
            
            int i_dialogNodeID = Integer.parseInt(i_dialogNode.getAttribute("id"));
            String i_dialogNodePrompt = substituteDialogString(i_dialogNode.getAttribute("prompt"));
            
            NodeList i_replyNodes = i_dialogNode.getElementsByTagName("reply");
            
            DialogReply[] i_replies = new DialogReply[i_replyNodes.getLength()];
            
            for (int o = 0; o < i_replyNodes.getLength(); o++)
            {
                Element i_replyNode = (Element)i_replyNodes.item(o);
                
                int i_replyNodeID = Integer.parseInt(i_replyNode.getAttribute("id"));
                String i_replyNodePrompt = substituteDialogString(i_replyNode.getAttribute("prompt"));
                
                NodeList i_actionNodes = i_replyNode.getElementsByTagName("action");
                NodeList i_replyNodeConditionNodes = i_replyNode.getElementsByTagName("replyCondition");
                
                DialogAction[] i_actions = new DialogAction[i_actionNodes.getLength()];
                DialogCondition[] i_replyNodeConditions = new DialogCondition[i_replyNodeConditionNodes.getLength()];
                
                for (int p = 0; p < i_replyNodeConditionNodes.getLength(); p++)
                {
                    Element i_replyNodeConditionNode = (Element)i_replyNodeConditionNodes.item(p);
                    
                    int i_replyNodeConditionNodeID = Integer.parseInt(i_replyNodeConditionNode.getAttribute("id"));
                    int i_replyNodeConditionNodeConditionID = Integer.parseInt(i_replyNodeConditionNode.getAttribute("conditionID"));
                    
                    i_replyNodeConditions[i_replyNodeConditionNodeID] = i_conditions[i_replyNodeConditionNodeConditionID];
                }
                
                for (int j = 0; j < i_actionNodes.getLength(); j++)
                {
                    Element i_actionNode = (Element)i_actionNodes.item(j);
                    
                    int i_actionNodeID = Integer.parseInt(i_actionNode.getAttribute("id"));
                    String i_actionNodeAction = i_actionNode.getAttribute("action");
                    String i_actionNodeArguments = i_actionNode.getAttribute("args");
                    
                    //Check which conditions apply to this action.
                    NodeList i_actionNodeConditionNodes = i_actionNode.getElementsByTagName("actionCondition");
                    DialogCondition[] i_actionNodeConditions = new DialogCondition[i_actionNodeConditionNodes.getLength()];
                    
                    for (int k = 0; k < i_actionNodeConditionNodes.getLength(); k++)
                    {
                        Element i_actionNodeConditionNode = (Element)i_actionNodeConditionNodes.item(k);
                        
                        int i_actionNodeConditionNodeID = Integer.parseInt(i_actionNodeConditionNode.getAttribute("id"));
                        int i_actionNodeConditionNodeConditionID = Integer.parseInt(i_actionNodeConditionNode.getAttribute("conditionID"));
                        
                        i_actionNodeConditions[i_actionNodeConditionNodeID] = i_conditions[i_actionNodeConditionNodeConditionID];
                    }
                    
                    i_actions[i_actionNodeID] = new DialogAction(i_actionNodeID, i_actionNodeAction, i_actionNodeArguments, i_actionNodeConditions);
                }
                
                i_replies[i_replyNodeID] = new DialogReply(i_replyNodeID, i_replyNodePrompt, i_actions, i_replyNodeConditions);
            }
            
            DialogNode dNode1 = new DialogNode(i_dialogNodeID, i_dialogNodePrompt, i_replies);
            
            dialog[i] = dNode1;
            
            System.out.println(dNode1);
        }
        
        setupDialog(gc);
    }
    
    public final String substituteDialogString(String stringIn)
    {
        Enumeration e = NPCSubstitution.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            stringIn = stringIn.replace("[" + key + "]", NPCSubstitution.getProperty(key));
        }
        
        return stringIn;
    }
    
    public void update(GameContainer gc, int delta) throws SlickException
    {
        if (dialogShowing)
            dGUI.update(gc, delta);
        
        if (!dialogShowing && Mouse.isButtonDown(0))
            beginDialog(gc);
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        if (dialogShowing)
            dGUI.render(gc, graphics);
    }
    
    public void dialogReplyClicked(GameContainer gc, int i) throws SlickException {
        // i = reply clicked
        
        DialogReply reply = dialog[curDialog].getReplyCM(i);
        System.out.println(reply);
        for (DialogAction action: reply.getActions())
        {
            System.out.println("ACTION: " + action);
            // Check if action conditions are met
            if (action.conditionsMet())
            {
                SaveData data = Game.getInstance().getCurrentGameData();
                switch (action.getAction())
                {
                    case "changeNode":
                    {
                        curDialog = Integer.parseInt(action.getArg(0));
                        dGUI.drawMenuContent(gc, dialog[curDialog].getPrompt(), dialog[curDialog].getReplyPrompts());
                        break;
                    }
                    case "intdata_decrease":
                    {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, data.getIntSaveData(iData) - modif);
                        break;
                    }
                    case "intdata_increase":
                    {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, data.getIntSaveData(iData) + modif);
                        break;
                    }
                    case "intdata_set":
                    {
                        int iData = Integer.parseInt(action.getArg(0));
                        int modif = Integer.parseInt(action.getArg(1));
                        data.setIntSaveData(iData, modif);
                        break;
                    }
                    case "endDialog":
                    {
                        endDialog();
                        break;
                    }
                }
            }
        }
    }
    
    public void setupDialog(GameContainer gc) throws SlickException
    {
        dGUI = new DialogGUI(gc, dialog, this);
    }
    
    public void beginDialog(GameContainer gc) throws SlickException
    {
        dialogShowing = true;
        
        curDialog = 0;
        dGUI.drawMenuContent(gc, dialog[0].getPrompt(), dialog[0].getReplyPrompts());
    }
    
    public void endDialog()
    {
        dialogShowing = false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the avatar
     */
    public Image getAvatar() {
        return avatar;
    }
}
