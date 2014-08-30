package com.adavieslyons.orthorpg.entities;

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
import org.newdawn.slick.geom.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.orthorpg.gui.DialogGUI;
import com.adavieslyons.util.SpriteSheet;
import com.adavieslyons.util.Vector2i;
import com.adavieslyons.util.XMLParser;
import com.adavieslyons.util.dialog.DialogAction;
import com.adavieslyons.util.dialog.DialogCondition;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.DialogReply;
import com.adavieslyons.util.dialog.Dialogable;
import com.adavieslyons.util.map.Map;

/**
 * 
 * @author Ashley
 */
public class NPC extends MovingPathEntity implements Dialogable {
	static final Properties NPCSubstitution;
	
	Document info;
	DialogGUI dGUI;
	private String name;
	private Image avatar;
	private boolean dialogShowing = false;
	
	static {
		NPCSubstitution = new Properties();
		try {
			NPCSubstitution.load(NPC.class.getClassLoader().getResourceAsStream("data/properties/NPCSubstitution.properties"));
		} catch (IOException ex) {
			Logger.getLogger(NPC.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public NPC(GameContainer gc, GameState game, int npcID, Map map, Vector2i path[]) throws SlickException {
		super(map);
		
		setPath(path);
		
		loadDataFromXML(gc, npcID, game);
	}
	
	public void loadDataFromXML(GameContainer gc, int npcID, GameState game) throws SlickException {
		info = XMLParser.instance.parseXML(this.getClass().getClassLoader().getResourceAsStream("data/xml/npc/" + npcID + ".xml"));
		
		name = info.getElementsByTagName("name").item(0).getTextContent();
		avatar = new Image("img/ui/avatar/npc/" + info.getElementsByTagName("avatar").item(0).getTextContent());
		Element textureElem = (Element) info.getElementsByTagName("texture").item(0);
		int spriteSheet = Integer.parseInt(textureElem.getElementsByTagName("spritesheet").item(0).getTextContent());
		int xPos = Integer.parseInt(textureElem.getElementsByTagName("xPos").item(0).getTextContent());
		int yPos = Integer.parseInt(textureElem.getElementsByTagName("yPos").item(0).getTextContent());
		image = SpriteSheet.getSpriteSheet(spriteSheet).getSubImage(xPos, yPos, 32, 64);

		Element dialogRoot = (Element) info.getElementsByTagName("dialog").item(0);
		NodeList dialogNodes = dialogRoot.getElementsByTagName("node");
		
		DialogNode[] dialog = new DialogNode[dialogNodes.getLength()];
		
		NodeList i_conditionNodes = dialogRoot.getElementsByTagName("condition");
		
		DialogCondition[] i_conditions = new DialogCondition[i_conditionNodes.getLength()];

		for (int l = 0; l < i_conditionNodes.getLength(); l++) {
			Element i_conditionNode = (Element) i_conditionNodes.item(l);
			
			int i_conditionNodeID = Integer.parseInt(i_conditionNode.getAttribute("id"));
			String i_conditionNodeCondition = i_conditionNode.getAttribute("condition");
			String i_conditionNodeArguments = i_conditionNode.getAttribute("args");
			
			i_conditions[i_conditionNodeID] = new DialogCondition(i_conditionNodeID, i_conditionNodeCondition, i_conditionNodeArguments);
		}

		for (int i = 0; i < dialogNodes.getLength(); i++) {
			Element i_dialogNode = (Element) dialogNodes.item(i);
			
			int i_dialogNodeID = Integer.parseInt(i_dialogNode.getAttribute("id"));
			String i_dialogNodePrompt = substituteDialogString(i_dialogNode.getAttribute("prompt"));
			
			NodeList i_replyNodes = i_dialogNode.getElementsByTagName("reply");
			
			DialogReply[] i_replies = new DialogReply[i_replyNodes.getLength()];
			
			for (int o = 0; o < i_replyNodes.getLength(); o++) {
				Element i_replyNode = (Element) i_replyNodes.item(o);
				
				int i_replyNodeID = Integer.parseInt(i_replyNode.getAttribute("id"));
				String i_replyNodePrompt = substituteDialogString(i_replyNode.getAttribute("prompt"));
				
				NodeList i_actionNodes = i_replyNode.getElementsByTagName("action");
				NodeList i_replyNodeConditionNodes = i_replyNode.getElementsByTagName("replyCondition");
				
				DialogAction[] i_actions = new DialogAction[i_actionNodes.getLength()];
				DialogCondition[] i_replyNodeConditions = new DialogCondition[i_replyNodeConditionNodes.getLength()];
				
				for (int p = 0; p < i_replyNodeConditionNodes.getLength(); p++) {
					Element i_replyNodeConditionNode = (Element) i_replyNodeConditionNodes.item(p);
					
					int i_replyNodeConditionNodeID = Integer.parseInt(i_replyNodeConditionNode.getAttribute("id"));
					int i_replyNodeConditionNodeConditionID = Integer.parseInt(i_replyNodeConditionNode.getAttribute("conditionID"));
					
					i_replyNodeConditions[i_replyNodeConditionNodeID] = i_conditions[i_replyNodeConditionNodeConditionID];
				}
				
				for (int j = 0; j < i_actionNodes.getLength(); j++) {
					Element i_actionNode = (Element) i_actionNodes.item(j);
					
					int i_actionNodeID = Integer.parseInt(i_actionNode.getAttribute("id"));
					String i_actionNodeAction = i_actionNode.getAttribute("action");
					String i_actionNodeArguments = i_actionNode.getAttribute("args");
					
					// Check which conditions apply to this action.
					NodeList i_actionNodeConditionNodes = i_actionNode.getElementsByTagName("actionCondition");
					DialogCondition[] i_actionNodeConditions = new DialogCondition[i_actionNodeConditionNodes.getLength()];
					
					for (int k = 0; k < i_actionNodeConditionNodes.getLength(); k++) {
						Element i_actionNodeConditionNode = (Element) i_actionNodeConditionNodes.item(k);
						
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
		}

		setupDialog(gc, game, dialog);
	}
	
	public void setupDialog(GameContainer gc, GameState game, DialogNode[] dialog) throws SlickException {
		dGUI = new DialogGUI(gc, game, dialog, this);
	}
	
	public final String substituteDialogString(String stringIn) {
		Enumeration<?> e = NPCSubstitution.propertyNames();
		
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			stringIn = stringIn.replace("[" + key + "]", NPCSubstitution.getProperty(key));
		}
		
		return stringIn;
	}
	
	public void update(GameContainer gc, GameState game, int delta) throws SlickException {
		if (dialogShowing)
			dGUI.update(gc, game, delta);
		
		if (!dialogShowing && Mouse.isButtonDown(0) && mouseOverThis(game)) {
			dGUI.beginDialog(gc, game);
			dialogShowing = true;
		}
		
		updateMove(delta);
		updatePath();
	}
	
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		super.render(gc, graphics);
		
		if (dialogShowing)
			dGUI.render(gc, graphics);
	}
	
	public boolean mouseOverThis(GameState game) {
		Rectangle rBounds = getRenderBounds();
		
		int mouseX = game.getInput().getMouseX();
		int mouseY = game.getInput().getMouseY();
		return (mouseX > rBounds.getMinX() && mouseX < rBounds.getMaxX() && mouseY > rBounds.getMinY() && mouseY < rBounds.getMaxY());
	}
	
	public String getName() {
		return name;
	}
	
	public Image getAvatar() {
		return avatar;
	}
	
	@Override
	public void dialogCloseRequested() {
		dialogShowing = false;
	}
	
	@Override
	public String getDialogTitle() {
		return name;
	}
	
	@Override
	public Image getDialogImage() {
		return avatar;
	}
}
