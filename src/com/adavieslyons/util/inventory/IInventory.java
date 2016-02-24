package com.adavieslyons.util.inventory;

public interface IInventory {
    ItemStack[] getItems();

    void addItem();

    void modifyStack();
}
