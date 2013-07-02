package com.sadwhalestudios.util;

import java.io.IOException;
import java.util.Properties;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author
 * Ashley
 */
public class SpriteSheet {
    static final Properties spriteSheetProperties;
    static final SpriteSheet[] spriteSheets;
    Image spriteSheet;
    
    
    static
    {
        spriteSheetProperties = new Properties();
        try {
            spriteSheetProperties.load(SpriteSheet.class.getClassLoader().getResourceAsStream("data/properties/Spritesheets.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        int count = Integer.parseInt(spriteSheetProperties.getProperty("totalCount"));
        spriteSheets = new SpriteSheet[count];
        
        for (int i = 0; i < count; i++)
        {
            try {
                spriteSheets[i] = new SpriteSheet(new Image("img/" + spriteSheetProperties.getProperty(Integer.toString(i))));
            } catch (SlickException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private SpriteSheet(Image image)
    {
        spriteSheet = image;
    }
    
    public Image getSubImage(int x, int y, int width, int height)
    {
        return spriteSheet.getSubImage(x, y, width, height);
    }
    
    public static SpriteSheet getSpriteSheet(int id)
    {
        return spriteSheets[id];
    }
}
