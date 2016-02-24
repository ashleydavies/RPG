package com.adavieslyons.util;

import org.newdawn.slick.Image;

/**
 * @author Ashley
 */
public class SpriteSheet {
    private static final Properties spriteSheetProperties;
    private static final SpriteSheet[] spriteSheets;

    static {
        spriteSheetProperties = FileLoader.getProperties("Spritesheets");

        int count = spriteSheetProperties.getIntProperty("totalCount");

        spriteSheets = new SpriteSheet[count];

        for (int i = 0; i < count; i++) {
            spriteSheets[i] = new SpriteSheet(
                    FileLoader.getImage(
                            spriteSheetProperties.getProperty(i)
                    )
            );
        }
    }

    private final Image spriteSheet;

    private SpriteSheet(Image image) {
        spriteSheet = image;
    }

    public static SpriteSheet getSpriteSheet(int id) {
        return spriteSheets[id];
    }

    public Image getSubImage(int x, int y, int width, int height) {
        return spriteSheet.getSubImage(x, y, width, height);
    }
}
