package com.sadwhalestudios.util;

/**
 *
 * @author Ashley
 */
public class SaveData {
    static final int MAX_INT_SAVE_DATA = 100;
    static final int MAX_BOOL_SAVE_DATA = 100;
    static final int MAX_STRING_SAVE_DATA = 100;
    
    int intSaveData[] = new int[MAX_INT_SAVE_DATA];
    boolean boolSaveData[] = new boolean[MAX_BOOL_SAVE_DATA];
    String stringSaveData[] = new String[MAX_STRING_SAVE_DATA];
    
    public int getIntSaveData(int id) {
        return intSaveData[id];
    }
    
    public void setIntSaveData(int id, int dt) {
        intSaveData[id] = dt;
    }
}
