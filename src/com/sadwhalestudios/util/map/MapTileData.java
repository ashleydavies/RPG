package com.sadwhalestudios.util.map;

/**
 *
 * @author
 * Ashley
 */
public class MapTileData {
    protected final int id;
    protected int data[];
    
    public MapTileData(int Id)
    {
        id = Id;
        data = new int[1];
        System.out.println("Tile Data Initialised: " + id);
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
