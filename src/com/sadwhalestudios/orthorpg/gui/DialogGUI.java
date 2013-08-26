package com.sadwhalestudios.orthorpg.gui;

import com.sadwhalestudios.orthorpg.entities.NPC;
import com.sadwhalestudios.orthorpg.gamestate.states.GameState;
import com.sadwhalestudios.util.dialog.DialogNode;

import java.awt.Font;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * @author
 * Ashley
 */
public final class DialogGUI extends GUIWindow {
    static TrueTypeFont dialogFont;
    static TrueTypeFont dialogTitleFont;
    
    
    
    NPC parent;
    boolean[] replyAreasMouseDown;
    MouseOverArea[] replyAreas;
    
    static {
        try {
            ui = new Image("img/ui/ui.png");
            dialogFont = new TrueTypeFont(new Font("Arial", Font.PLAIN, 16), true);
            dialogTitleFont = new TrueTypeFont(new Font("Arial", Font.BOLD, 36), true);
        } catch (SlickException e) {}
    }
    
    public DialogGUI(GameContainer gc, GameState game, DialogNode[] dialog, NPC parent) throws SlickException {
    	super(gc, game, 504, 624);
    	
        this.parent = parent;
        
        String content = dialog[0].getPrompt();
        String[] responses = dialog[0].getReplyPrompts(game);
        
        renderPrimaryContent(gc, content, responses);
    }
    
    public void renderPrimaryContent(GameContainer gc, String content, String[] responses) throws SlickException {
        content = prepareString(content);
        responses = prepareStrings(responses);
        
        replyAreas = new MouseOverArea[responses.length];
        replyAreasMouseDown = new boolean[responses.length];
        
        Graphics graphics = gc.getGraphics();
        
        graphics.clear();
        graphics.drawImage(parent.getAvatar(), 12, 12);
        
        graphics.setColor(Color.black);
        graphics.setFont(dialogTitleFont);
        graphics.drawString(parent.getName(), 12 + 64, 12);
        
        int y = (int) (12 + 32);
        
        graphics.setFont(dialogFont);
        for (String line : content.split("\n"))
            graphics.drawString(line, 12 + 64, y += 18);
        
        graphics.setColor(Color.blue);
        y += 36;
        
        int replyN = 0;
        
        for (String reply : responses) {
            int lineN = 0;
            for (String line : reply.split("\n")) {
                if (lineN == 0)
                    graphics.drawString(++replyN + ": " + line, 12 + 64 - dialogFont.getWidth(replyN + ": "), y += 18);
                else
                    graphics.drawString(line, 12 + 64, y += 18);
                
                lineN++;
            }
            
            replyAreas[replyN - 1] = new MouseOverArea(gc, new Image((int) (windowRect.getWidth() - 24 - 64), 18 * lineN), (int) (windowRect.getX() + 12 + 64), (int) (windowRect.getY() + y - ((lineN - 1) * 18)));
            replyAreasMouseDown[replyN - 1] = false;
        }
        
        graphics.copyArea(windowDynamicContent, 0, 0);
        graphics.clear();
    }
    
    public final String[] prepareStrings(String[] content) {
        String[] retArray = new String[content.length];
        int i = 0;
        for (String str: content)
            retArray[i++] = prepareString(str);
        
        return retArray;
    }
    
    public final String prepareString(String content) {
        String prepString = " ";
        content = content.replace("[N]", "\n");
        String[] content_words = content.split(" ");
        
        int curLine = 1;
        
        for (String word: content_words) {
            if ("\n".equals(word)) {
                prepString += "\n";
                curLine = 0;
                continue;
            }
            
            curLine += dialogFont.getWidth(word);
            
            if (curLine > windowRect.getWidth() - 42 - 64 - 42) {
                curLine = dialogFont.getWidth(word);
                prepString += "\n";
            }
            prepString += word + " ";
        }
        
        return prepString;
    }
    
    public void update(GameContainer gc, GameState game, int delta) throws SlickException {
        for (int i = 0; i < replyAreas.length; i++) {
            if (replyAreas[i].isMouseOver() && Mouse.isButtonDown(0)) {
                replyAreasMouseDown[i] = true;
            }
            else if (replyAreas[i].isMouseOver() && !Mouse.isButtonDown(0) && replyAreasMouseDown[i] == true) {
                replyAreasMouseDown[i] = false;
                
                System.out.println("Clicked " + i);
                parent.dialogReplyClicked(gc, game, i);
                
                renderDefaultContent(gc, game);
                
                break;
            }
            
            if (!replyAreas[i].isMouseOver()) {
                replyAreasMouseDown[i] = false;
            }
        }
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException {
        graphics.drawImage(windowBg, windowRect.getX(), windowRect.getY());
        graphics.drawImage(windowDefaultContent, windowRect.getX(), windowRect.getY());
        graphics.drawImage(windowDynamicContent, windowRect.getX(), windowRect.getY());
    }
}
