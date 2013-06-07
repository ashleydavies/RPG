package com.sadwhalestudios.util;

/**
 *
 * @author Ashley
 */
public class SaveData {
    static final int MAX_INT_SAVE_DATA = 100;
    static final int MAX_BOOL_SAVE_DATA = 100;
    static final int MAX_STRING_SAVE_DATA = 100;
    
    int intSaveData[];
    Boolean boolSaveData[];
    String stringSaveData[];
    
    public void SaveData()
    {
        intSaveData = new int[MAX_INT_SAVE_DATA];
        boolSaveData = new Boolean[MAX_BOOL_SAVE_DATA];
        stringSaveData = new String[MAX_STRING_SAVE_DATA];
    }
}
