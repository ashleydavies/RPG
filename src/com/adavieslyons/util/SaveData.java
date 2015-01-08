package com.adavieslyons.util;

import java.util.Properties;

/**
 * @author Ashley
 */
public class SaveData {
    static final Properties FriendlyNames;
    static final int MAX_INT_SAVE_DATA = 100;
    static final int MAX_BOOL_SAVE_DATA = 100;
    static final int MAX_STRING_SAVE_DATA = 100;

    static {
        FriendlyNames = new Properties();
        try {
            FriendlyNames.load(SaveData.class.getClassLoader()
                    .getResourceAsStream(
                            "data/properties/IntDataFriendlyNames.properties"));
        } catch (Exception e) {
        }
    }

    int intSaveData[] = new int[MAX_INT_SAVE_DATA];
    boolean boolSaveData[] = new boolean[MAX_BOOL_SAVE_DATA];
    String stringSaveData[] = new String[MAX_STRING_SAVE_DATA];

    public static String getFriendlyName(int id) {
        return FriendlyNames.getProperty(Integer.toString(id));
    }

    public int getIntSaveData(int id) {
        return intSaveData[id];
    }

    public void setIntSaveData(int id, int dt) {
        intSaveData[id] = dt;
    }
}
