package com.adavieslyons.util.inventory;

public class ItemStack {
    private final int id;
    private int quantity;

    public ItemStack(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Item getItem() {
        return Item.getItem(id);
    }

    public int getID() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
