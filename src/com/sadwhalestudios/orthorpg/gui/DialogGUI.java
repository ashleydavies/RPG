package com.sadwhalestudios.orthorpg.gui;

import com.sadwhalestudios.orthorpg.Game;
import com.sadwhalestudios.orthorpg.entities.NPC;
import com.sadwhalestudios.util.dialog.DialogNode;
import java.awt.Font;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * @author
 * Ashley
 */
public final class DialogGUI {
    static Image ui;
    static TrueTypeFont dialogFont;
    static TrueTypeFont dialogTitleFont;
    static int width, height;
    
    Point position;
    Image menu;
    Image menuContent;
    Image menuPrimaryContent;
    NPC parent;
    boolean[] replyAreasMouseDown;
    MouseOverArea[] replyAreas;
    
    static
    {
        try {
            ui = new Image("img/ui/ui.png");
            dialogFont = new TrueTypeFont(new Font("Arial", Font.PLAIN, 16), true);
            dialogTitleFont = new TrueTypeFont(new Font("Arial", Font.BOLD, 36), true);
            
            width = 504;
            height = 624; // TODO : Make GUI sizing dynamic based on resolution
        } catch (SlickException e) {}
    }
    
    public DialogGUI(GameContainer gc, DialogNode[] dialog, NPC parent) throws SlickException {
        this.parent = parent;
        
        int x = gc.getWidth() / 2 - width / 2;
        int y = gc.getHeight() / 2 - height / 2;
        
        position = new Point(x, y);
        menu = new Image(width, height);
        menuContent = new Image(width, height);
        menuPrimaryContent = new Image(width, height);
        
        String content = dialog[0].getPrompt();
        String[] responses = dialog[0].getReplyPrompts();
        
        drawMenu(gc, new Rectangle(x, y, width, height));
        drawMenuPrimaryContent(gc);
        drawMenuContent(gc, content, responses);
    }
    
    public void drawMenu(GameContainer gc, Rectangle rect)
    {
        Image border_tl = ui.getSubImage(0, 0, 12, 12);
        Image border_tr = ui.getSubImage(12, 0, 12, 12);
        Image border_bl = ui.getSubImage(0, 12, 12, 12);
        Image border_br = ui.getSubImage(12, 12, 12, 12);
        Image border_t = ui.getSubImage(12, 24, 12, 12);
        Image border_l = ui.getSubImage(0, 24, 12, 12);
        Image border_r = ui.getSubImage(12, 36, 12, 12);
        Image border_b = ui.getSubImage(0, 36, 12, 12);
        Image inner = ui.getSubImage(24, 0, 60, 60);
        
        Graphics graphics = gc.getGraphics();
        
        graphics.clear();
        
        graphics.drawImage(border_tl, 0, 0);
        graphics.drawImage(border_tr, rect.getWidth() - 12, 0);
        graphics.drawImage(border_bl, 0, rect.getHeight() - 12);
        graphics.drawImage(border_br, rect.getWidth() - 12, rect.getHeight() - 12);
        
        for (int i = 1; i <= (rect.getWidth() - 24) / 12; i++)
        {
            graphics.drawImage(border_t, 12 * i, 0);
            graphics.drawImage(border_b, 12 * i, rect.getHeight() - 12);
        }
        
        for (int i = 1; i <= (rect.getHeight() - 24) / 12; i++)
        {
            graphics.drawImage(border_l, 0, 12 * i);
            graphics.drawImage(border_r, rect.getWidth() - 12, 12 * i);
        }
        
        for (int x = 0; x < (rect.getWidth() - 24) / 60; x++)
            for (int y = 0; y < (rect.getHeight() - 24) / 60; y++)
                graphics.drawImage(inner, 60 * x + 12, 60 * y + 12);
        
        graphics.copyArea(menu, 0, 0);
        graphics.clear();
    }
    
    
    public void drawMenuPrimaryContent(GameContainer gc)
    {
        Graphics graphics = gc.getGraphics();
        graphics.clear();
        
        // Draw standard menu UI (Todo: Move to a higher-tier GUI class and copy down)
        graphics.setColor(Color.black);
        graphics.drawString(Game.getInstance().getCurrentGameData().getIntSaveData(0) + " coins", 24, height - 36);
        
        graphics.copyArea(menuPrimaryContent, 0, 0);
        graphics.clear();
    }
    
    public void drawMenuContent(GameContainer gc, String content, String[] responses) throws SlickException
    {
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
        
        for (String reply : responses)
        {
            int lineN = 0;
            for (String line : reply.split("\n"))
            {
                if (lineN == 0)
                    graphics.drawString(++replyN + ": " + line, 12 + 64 - dialogFont.getWidth(replyN + ": "), y += 18);
                else
                    graphics.drawString(line, 12 + 64, y += 18);
                
                lineN++;
            }
            
            replyAreas[replyN - 1] = new MouseOverArea(gc, new Image(width - 24 - 64, 18 * lineN), (int) (position.getX() + 12 + 64), (int) (position.getY() + y - ((lineN - 1) * 18)));
            replyAreasMouseDown[replyN - 1] = false;
        }
        
        graphics.copyArea(menuContent, 0, 0);
        graphics.clear();
    }
    
    public final String[] prepareStrings(String[] content)
    {
        String[] retArray = new String[content.length];
        int i = 0;
        for (String str: content)
            retArray[i++] = prepareString(str);
        
        return retArray;
    }
    
    public final String prepareString(String content)
    {
        String prepString = " ";
        content = content.replace("[N]", "\n");
        String[] content_words = content.split(" ");
        
        int curLine = 1;
        
        for (String word: content_words)
        {
            if ("\n".equals(word))
            {
                prepString += "\n";
                curLine = 0;
                continue;
            }
            
            curLine += dialogFont.getWidth(word);
            //System.out.println("Word: " + word + " curLine: " + curLine);
            if (curLine > width - 42 - 64 - 42)
            {
                curLine = dialogFont.getWidth(word);
                prepString += "\n";
            }
            prepString += word + " ";
        }
        
        return prepString;
    }
    
    public void update(GameContainer gc, int delta) throws SlickException
    {
        for (int i = 0; i < replyAreas.length; i++)
        {
            if (replyAreas[i].isMouseOver() && Mouse.isButtonDown(0))
            {
                replyAreasMouseDown[i] = true;
            }
            else if (replyAreas[i].isMouseOver() && !Mouse.isButtonDown(0) && replyAreasMouseDown[i] == true)
            {
                replyAreasMouseDown[i] = false;
                
                System.out.println("Clicked " + i);
                parent.dialogReplyClicked(gc, i);
                
                drawMenuPrimaryContent(gc);
                
                break;
            }
            
            if (!replyAreas[i].isMouseOver())
            {
                replyAreasMouseDown[i] = false;
            }
        }
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        graphics.drawImage(menu, position.getX(), position.getY());
        graphics.drawImage(menuPrimaryContent, position.getX(), position.getY());
        graphics.drawImage(menuContent, position.getX(), position.getY());
    }
}
