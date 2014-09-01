package com.adavieslyons.util.inventory;

public interface IInventory {
	public ItemStack[] getItems();

	public void addItem();

	public void modifyStack();
}
