package com.adavieslyons.orthorpg.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.adavieslyons.orthorpg.gamestate.states.GameState;

public abstract class GUIWindow {
	static final int BW = 12; // Border Width
	
	static Image ui;
	
	Rectangle windowRect;
	Image windowBg;
	Image windowDefaultContent;
	Image windowDynamicContent;
	
    static {
        try {
            ui = new Image("img/ui/ui.png");
        } catch (SlickException e) {}
    }
	
	public GUIWindow(GameContainer gc, GameState game, int width, int height) throws SlickException {		
		int x = gc.getWidth() / 2 - width / 2;
        int y = gc.getHeight() / 2 - height / 2;
        
        windowRect = new Rectangle(x, y, width, height);
        
		windowBg = new Image(width, height);
        windowDefaultContent = new Image(width, height);
        windowDynamicContent = new Image(width, height);
        
        renderWindow(gc);
        renderDefaultContent(gc, game);
	}
	
	public void renderWindow(GameContainer gc) {
		Image border_tl = ui.getSubImage(0, 0, BW, BW);
        Image border_tr = ui.getSubImage(BW, 0, BW, BW);
        Image border_bl = ui.getSubImage(0, BW, BW, BW);
        Image border_br = ui.getSubImage(BW, BW, BW, BW);
        Image border_t = ui.getSubImage(BW, BW * 2, BW, BW);
        Image border_l = ui.getSubImage(0, BW * 2, BW, BW);
        Image border_r = ui.getSubImage(BW, BW * 3, BW, BW);
        Image border_b = ui.getSubImage(0, BW * 3, BW, BW);
        Image inner = ui.getSubImage(BW * 2, 0, BW * 5, BW * 5);
        
        Graphics graphics = gc.getGraphics();
        
        graphics.clear();
        
        graphics.drawImage(border_tl, 0, 0);
        graphics.drawImage(border_tr, windowRect.getWidth() - BW, 0);
        graphics.drawImage(border_bl, 0, windowRect.getHeight() - BW);
        graphics.drawImage(border_br, windowRect.getWidth() - BW, windowRect.getHeight() - BW);
        
        for (int i = 1; i <= (windowRect.getWidth() - BW * 2) / BW; i++) {
            graphics.drawImage(border_t, BW * i, 0);
            graphics.drawImage(border_b, BW * i, windowRect.getHeight() - BW);
        }
        
        for (int i = 1; i <= (windowRect.getHeight() - BW * 2) / BW; i++) {
            graphics.drawImage(border_l, 0, BW * i);
            graphics.drawImage(border_r, windowRect.getWidth() - BW, BW * i);
        }
        
        for (int x = 0; x < (windowRect.getWidth() - BW * 2) / BW * 5; x++)
            for (int y = 0; y < (windowRect.getHeight() - BW * 2) / BW * 5; y++)
                graphics.drawImage(inner, BW * 5 * x + BW, BW * 5 * y + BW);
        
        graphics.copyArea(windowBg, 0, 0);
        graphics.clear();
	}
	
	public void renderDefaultContent(GameContainer gc, GameState game) {
        Graphics graphics = gc.getGraphics();
        graphics.clear();
        
        // Draw standard menu UI
        graphics.setColor(Color.black);
        graphics.drawString(game.getCurrentGameData().getIntSaveData(0) + " coins", 24, windowRect.getHeight() - 36);
        
        graphics.copyArea(windowDefaultContent, 0, 0);
        graphics.clear();
	}
}
