package com.sadwhalestudios.orthorpg.gui;

import java.awt.Font;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

/**
 *
 * @author
 * Ashley
 */
public class DialogGUI {
    Point position;
    int width;
    Image ui;
    Image menu;
    Image speaker;
    TrueTypeFont dialogFont;
    TrueTypeFont dialogTitleFont;
    String content;
    
    public DialogGUI(GameContainer gc) throws SlickException {
        ui = new Image("resources/img/ui/ui.png");
        speaker = new Image("resources/img/ui/avatar/npc/fred.png");
        
        int width = 504;
        this.width = width;
        int height = 624;
        
        int x = gc.getWidth() / 2 - width / 2;
        int y = gc.getHeight() / 2 - height / 2;
        
        position = new Point(x, y);        
        menu = new Image(width, height);
        
        dialogFont = new TrueTypeFont(new Font("Arial", Font.PLAIN, 16), true);
        dialogTitleFont = new TrueTypeFont(new Font("Arial", Font.BOLD, 36), true);
        
        drawMenu(gc, new Rectangle(x, y, width, height));
        prepareContent("Farmer Joe glances in your direction, looking at youu with uncertainty. 'Hello there!'");
    }
    
    public void update(GameContainer gc, int delta) throws SlickException
    {
        
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
    
    public void prepareContent(String content)
    {
        String prepString = "";
        String[] content_words = content.split(" ");
        
        int curLine = 0;
        
        for (String word: content_words)
        {
            curLine += dialogFont.getWidth(word);
            if (curLine > width - 42 - 64 - 24)
            {
                curLine = 0;
                prepString += "\r\n";
            }
            prepString += word + " ";
        }
        
        this.content = prepString;
    }
    
    public void render(GameContainer gc, Graphics graphics) throws SlickException
    {
        //renderWindow(gc, graphics);
        graphics.drawImage(menu, position.getX(), position.getY());
        graphics.drawImage(speaker, position.getX() + 12, position.getY() + 12);
        
        graphics.setColor(Color.black);
        graphics.setFont(dialogTitleFont);
        graphics.drawString("Farmer Joe", position.getX() + 12 + 64, position.getY() + 12);
        
        int y = (int) (position.getY() + 12 + 32);
        
        graphics.setFont(dialogFont);
        for (String line : content.split("\n"))
            graphics.drawString(line, position.getX() + 12 + 64, y += 18);
    }
}
