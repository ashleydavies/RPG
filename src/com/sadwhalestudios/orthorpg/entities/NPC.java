package com.sadwhalestudios.orthorpg.entities;

import com.sadwhalestudios.orthorpg.gui.DialogGUI;
import com.sadwhalestudios.util.DialogAction;
import com.sadwhalestudios.util.DialogNode;
import com.sadwhalestudios.util.DialogReply;
import com.sadwhalestudios.util.FileUtility;
import com.sadwhalestudios.util.XMLParser;
import java.io.File;
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
    Document info;
    DialogNode[] dialog;
    DialogGUI dGUI;
    private String name;
    private Image avatar;
    int curDialog = 0;
    
    public NPC(GameContainer gc, int npcID) throws SlickException
    {
        String startsWith = FileUtility.instance.IDFormat(npcID, 5) + "NPC";
        
        File infoXML = FileUtility.instance.getFileStartingWith("resources/data/xml", startsWith);

        
        info = XMLParser.instance.parseXML(infoXML);
        
        
        // TODO: Sort this out
        
        name = info.getElementsByTagName("name").item(0).getTextContent();
        avatar = new Image("resources/img/ui/avatar/npc/" + info.getElementsByTagName("avatar").item(0).getTextContent());
        
        Element dialogRoot = (Element)info.getElementsByTagName("dialog").item(0);
        NodeList dialogNodes = dialogRoot.getElementsByTagName("node");
        
        dialog = new DialogNode[dialogNodes.getLength()];
        
        for (int i = 0; i < dialogNodes.getLength(); i += 1)
        {
            Element i_dialogNode = (Element)dialogNodes.item(i);
            
            int i_dialogNodeID = Integer.parseInt(i_dialogNode.getAttribute("id"));
            String i_dialogNodePrompt = i_dialogNode.getAttribute("prompt");
            
            NodeList i_replyNodes = i_dialogNode.getElementsByTagName("reply");
            
            DialogReply[] i_replies = new DialogReply[i_replyNodes.getLength()];
            
            for (int o = 0; o < i_replyNodes.getLength(); o++)
            {
                Element i_replyNode = (Element)i_replyNodes.item(o);
                
                int i_replyNodeID = Integer.parseInt(i_replyNode.getAttribute("id"));
                String i_replyNodePrompt = i_replyNode.getAttribute("prompt");
                
                NodeList i_actionNodes = i_replyNode.getElementsByTagName("action");
                
                DialogAction[] i_actions = new DialogAction[i_actionNodes.getLength()];
                
                for (int j = 0; j < i_actionNodes.getLength(); j++)
                {
                    Element i_actionNode = (Element)i_actionNodes.item(j);
                    
                    int i_actionNodeID = Integer.parseInt(i_actionNode.getAttribute("id"));
                    String i_actionNodePrompt = i_actionNode.getAttribute("action");
                    String i_actionNodeArguments = i_actionNode.getAttribute("args");
                    
                    i_actions[i_actionNodeID] = new DialogAction(i_actionNodeID, i_actionNodePrompt, i_actionNodeArguments);
                }
                
                i_replies[i_replyNodeID] = new DialogReply(i_replyNodeID, i_replyNodePrompt, i_actions);
            }
            
            DialogNode dNode1 = new DialogNode(i_dialogNodeID, i_dialogNodePrompt, i_replies);
            
            dialog[i] = dNode1;
            
            System.out.println(dNode1);
        }
        
        dGUI = new DialogGUI(gc, dialog, this);
    }
    
    public void init(GameContainer gc) throws SlickException
    {
        
    }
    
    public void update(GameContainer gc, int delta) throws SlickException
    {
        dGUI.update(gc, delta);
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        dGUI.render(gc, graphics);
    }
    
    public void dialogReplyClicked(GameContainer gc, int i) throws SlickException {
        // i = reply clicked
        
        DialogReply reply = dialog[curDialog].getReply(i);
        
        for (DialogAction action: reply.getActions())
        {
            switch (action.getAction())
            {
                case "changeNode":
                    curDialog = Integer.parseInt(action.getArg(0));
                    dGUI.drawMenuContent(gc, dialog[curDialog].getPrompt(), dialog[curDialog].getReplyPrompts());
            }
        }
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
