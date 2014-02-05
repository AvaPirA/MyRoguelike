package com.avapir.roguelike.locatable;

/**
 * User: Alpen Ditrix Date: 27.01.14 Time: 20:36
 */
public class Item {
    private int ID;
    private int amount;

    public Item(int ID) {
        this.ID = ID;
        amount = 0;
    }

    public Item(Item item) {
        ID = item.ID;
        amount = 0;
    }

    public Item(int ID, int amount) {
        this.ID = ID;
        this.amount = amount;
    }

    public Item(Item item, int amount) {
        ID = item.ID;
        this.amount = amount;
    }

    public ItemData getData() {
        return ItemDatabase.get(ID);
    }

    public void convert(Item item) {
        ID = item.ID;
    }

    public void copy(Item item) {
        ID = item.ID;
        amount = item.amount;
    }

    public synchronized void swap(Item item) {
        int tmpID = ID;
        int tmpAmount = amount;
        ID = item.ID;
        amount = item.amount;
        item.ID = tmpID;
        item.amount = tmpAmount;
    }

    public int getID() {
        return ID;
    }

    public int getAmount() {
        return amount;
    }

    public void increase(int n) {
        amount+=n;
    }

    public void decrease(int n) {
        amount = Math.max(amount - n, 0);
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !(obj instanceof Item)) && ((Item) obj).ID == ID;
    }
}
