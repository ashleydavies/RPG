package com.adavieslyons.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adavieslyons.orthorpg.entities.Mob;
import com.adavieslyons.util.dialog.DialogAction;
import com.adavieslyons.util.dialog.DialogCondition;
import com.adavieslyons.util.dialog.DialogNode;
import com.adavieslyons.util.dialog.DialogReply;

/**
 * 
 * @author Ashley
 */
public final class XMLParser {
	static DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			.newInstance();
	static DocumentBuilder dBuilder;
	static final Properties ChatSubstitution;

	static {
		ChatSubstitution = new Properties();
		try {
			ChatSubstitution.load(Mob.class.getClassLoader()
					.getResourceAsStream(
							"data/properties/NPCSubstitution.properties"));
		} catch (IOException ex) {
			Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static XMLParser instance = new XMLParser();

	private XMLParser() {
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (Exception e) {
		}
	}

	public Document parseXML(File file) {
		try {
			return dBuilder.parse(file);
		} catch (Exception e) {
		}

		return null;
	}

	public Document parseXML(InputStream file) {
		try {
			return dBuilder.parse(file);
		} catch (Exception e) {
		}

		return null;
	}

	/* Static helpers */
	public static String substituteDialogString(String stringIn) {
		Enumeration<?> e = ChatSubstitution.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			stringIn = stringIn.replace("[" + key + "]",
					ChatSubstitution.getProperty(key));
		}

		return stringIn;
	}

	//
	// Loads a texture from a <texture> node
	//
	public static Image loadTexture(Element rootNode) {
		int spriteSheet = Integer.parseInt(rootNode
				.getElementsByTagName("spritesheet").item(0).getTextContent());
		int xPos = Integer.parseInt(rootNode.getElementsByTagName("xPos")
				.item(0).getTextContent());
		int yPos = Integer.parseInt(rootNode.getElementsByTagName("yPos")
				.item(0).getTextContent());
		return SpriteSheet.getSpriteSheet(spriteSheet).getSubImage(xPos, yPos,
				32, 64);
	}

	//
	// Loads dialog from a <dialog> node
	//
	public static DialogNode[] loadDialog(Element rootNode) {
		NodeList dialogNodes = rootNode.getElementsByTagName("node");
		NodeList conditionNodes = rootNode.getElementsByTagName("condition");

		DialogNode[] dialog = new DialogNode[dialogNodes.getLength()];
		DialogCondition[] conditions = new DialogCondition[conditionNodes
				.getLength()];

		for (int i = 0; i < conditionNodes.getLength(); i++) {
			Element conditionNode = (Element) conditionNodes.item(i);

			int conditionNodeID = Integer.parseInt(conditionNode
					.getAttribute("id"));
			String conditionNodeCondition = conditionNode
					.getAttribute("condition");
			String conditionNodeArguments = conditionNode.getAttribute("args");

			conditions[conditionNodeID] = new DialogCondition(conditionNodeID,
					conditionNodeCondition, conditionNodeArguments);
		}

		for (int i = 0; i < dialogNodes.getLength(); i++) {
			Element dialogNode = (Element) dialogNodes.item(i);

			int dialogNodeID = Integer.parseInt(dialogNode.getAttribute("id"));
			String dialogNodePrompt = substituteDialogString(dialogNode
					.getAttribute("prompt"));

			NodeList replyNodes = dialogNode.getElementsByTagName("reply");

			DialogReply[] replies = new DialogReply[replyNodes.getLength()];

			for (int o = 0; o < replyNodes.getLength(); o++) {
				Element replyNode = (Element) replyNodes.item(o);

				int replyNodeID = Integer
						.parseInt(replyNode.getAttribute("id"));
				String replyNodePrompt = substituteDialogString(replyNode
						.getAttribute("prompt"));

				NodeList actionNodes = replyNode.getElementsByTagName("action");
				NodeList replyNodeConditionNodes = replyNode
						.getElementsByTagName("replyCondition");

				DialogAction[] actions = new DialogAction[actionNodes
						.getLength()];
				DialogCondition[] replyNodeConditions = new DialogCondition[replyNodeConditionNodes
						.getLength()];

				for (int p = 0; p < replyNodeConditionNodes.getLength(); p++) {
					Element replyNodeConditionNode = (Element) replyNodeConditionNodes
							.item(p);

					int replyNodeConditionNodeID = Integer
							.parseInt(replyNodeConditionNode.getAttribute("id"));
					int replyNodeConditionNodeConditionID = Integer
							.parseInt(replyNodeConditionNode
									.getAttribute("conditionID"));

					replyNodeConditions[replyNodeConditionNodeID] = conditions[replyNodeConditionNodeConditionID];
				}

				for (int j = 0; j < actionNodes.getLength(); j++) {
					Element actionNode = (Element) actionNodes.item(j);

					int actionNodeID = Integer.parseInt(actionNode
							.getAttribute("id"));
					String actionNodeAction = actionNode.getAttribute("action");
					String actionNodeArguments = actionNode
							.getAttribute("args");

					// Check which conditions apply to this action.
					NodeList actionNodeConditionNodes = actionNode
							.getElementsByTagName("actionCondition");
					DialogCondition[] actionNodeConditions = new DialogCondition[actionNodeConditionNodes
							.getLength()];

					for (int k = 0; k < actionNodeConditionNodes.getLength(); k++) {
						Element i_actionNodeConditionNode = (Element) actionNodeConditionNodes
								.item(k);

						int i_actionNodeConditionNodeID = Integer
								.parseInt(i_actionNodeConditionNode
										.getAttribute("id"));
						int i_actionNodeConditionNodeConditionID = Integer
								.parseInt(i_actionNodeConditionNode
										.getAttribute("conditionID"));

						actionNodeConditions[i_actionNodeConditionNodeID] = conditions[i_actionNodeConditionNodeConditionID];
					}

					actions[actionNodeID] = new DialogAction(actionNodeID,
							actionNodeAction, actionNodeArguments,
							actionNodeConditions);
				}

				replies[replyNodeID] = new DialogReply(replyNodeID,
						replyNodePrompt, actions, replyNodeConditions);
			}

			DialogNode completedNode = new DialogNode(dialogNodeID,
					dialogNodePrompt, replies);

			dialog[i] = completedNode;
		}

		return dialog;
	}
}
