package com.adavieslyons.util.map;

/**
 * 
 * @author Ashley
 */
public class MapTileData {
	private final int id;
	protected int data[];

	public MapTileData(int Id) {
		id = Id;
		data = new int[1];
	}

	public void setData(int dataID, int setData) {
		data[dataID] = setData;
	}

	public int getData(int dataID) {
		return data[dataID];
	}
	
	public int getId() {
		return id;
	}
}
