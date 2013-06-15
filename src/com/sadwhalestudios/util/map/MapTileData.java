package com.sadwhalestudios.util.map;

/**
 *
 * @author
 * Ashley
 */
public class MapTileData {
    protected int data[];
    
    public MapTileData(int dataLength)
    {
        data = new int[dataLength];
    }
    
    public void setData(int dataID, int setData)
    {
        data[dataID] = setData;
    }
    
    public int getData(int dataID)
    {
        return data[dataID];
    }
}
