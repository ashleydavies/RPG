package uk.daviesl.rpg.util.inventory;

public interface IInventory {
    ItemStack[] getItems();

    void addItem();

    void modifyStack();
}
