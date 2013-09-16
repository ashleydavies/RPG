package com.adavieslyons.util.dialog;

import com.adavieslyons.orthorpg.gamestate.states.GameState;
import com.adavieslyons.util.dialog.DialogAction;

/**
 * 
 * @author Ashley
 */
public class DialogReply {
	final int id;
	private final String prompt;
	
	private final DialogAction[] actions;
	private final DialogCondition[] conditions;
	
	public DialogReply(int id, String prompt, DialogAction[] actions, DialogCondition[] conditions) {
		this.id = id;
		this.prompt = prompt;
		this.actions = actions;
		this.conditions = conditions;
	}
	
	@Override
	public String toString() {
		String retString = getPrompt() + "\n        =ACTIONS= (" + getActions().length + "):\n          ";
		
		for (DialogAction dA : getActions())
			retString += "[" + dA + "],";
		
		return retString;
	}
	
	public boolean conditionsMet(GameState game) {
		for (DialogCondition condition : conditions) {
			if (!condition.conditionMet(game)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @return the prompt
	 */
	public String getPrompt() {
		return prompt;
	}
	
	/**
	 * @return the actions
	 */
	public DialogAction[] getActions() {
		return actions;
	}
	
	/**
	 * @return the conditions
	 */
	public DialogCondition[] getConditions() {
		return conditions;
	}
}
