package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.game.world.items.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * That is representation of hero's backpack.<br/>
 * You can put here items, you can get items from herem you can move items inside the inventory,
 * you can stack similar items into one cell, you can split one cell into two,
 * you can enlarge inventory (maybe with some special items or NPC).
 */
public final class InventoryHandler {
    //todo inventory sorting
    //todo InventoryHandler is too binded with it's View. Need to escape this and to do it in right MVC way

    public static final int    LINE          = 8;
    private             int    inventorySize = 3;
    private             Item[] storage       = new Item[LINE * (inventorySize + 3)];
    /** Amount of cells which stores some items */
    private int occupied;

    public InventoryHandler() {
//            put(new Item(1));
//            put(new Item(2));
//            put(new Item(3));
//            put(new Item(4, 20));
    }

    public static int coordToCell(int x, int y) {
        return y * LINE + x;
    }

    public static int coordToCell(Point p) {
        return coordToCell(p.x, p.y);
    }

    /**
     * Adds new line ({@value #LINE} cells) to storage
     */
    public void enlarge() {
        Item[] tmp = new Item[LINE * (inventorySize)];
        System.arraycopy(storage, 0, tmp, 0, tmp.length); //save state
        storage = new Item[LINE * (++inventorySize)]; //enlarge
        System.arraycopy(tmp, 0, storage, 0, tmp.length); //restore
    }

    /**
     * Puts new item to inventory. If there is no items of that type, the new ones will be placed in first occurred
     * empty cell. Either they will be stacked.
     *
     * @param item acquired item(s)
     *
     * @throws IllegalArgumentException if there is no applicable cell
     */
    public synchronized void put(Item item) {
        if (free() == 0) {throw new IllegalArgumentException("No empty space");}
        int index = find(item);
        if (index < 0) {
            int firstEmpty = find(null);
            if (firstEmpty < 0) { throw new IllegalArgumentException("No empty space"); }
            storage[firstEmpty] = item;
            occupied++;
        } else {
            storage[index].increase(item.getAmount());
        }
    }

    /**
     * @param index index of cell for item to return
     *
     * @return item from specified cell
     */
    public Item get(int index) {
        return storage[index];
    }

    public Item get(Point p) {
        return get(coordToCell(p));
    }

    /**
     * @param item type of items
     *
     * @return amount of items of specified type
     */
    public synchronized int getAmount(Item item) {
        if (item == null) {
            return 0;
        }
        int sum = 0;
        for (Item i : storage) {
            if (item.equals(i)) {
                sum += i.getAmount();
            }
        }
        return sum;
    }

    /**
     * @param index index of cell to explore
     *
     * @return amount of items into the specified cell
     */
    public synchronized int getAmount(int index) {
        checkIndex(index);
        return storage[index] == null ? 0 : storage[index].getAmount();
    }

    /**
     * @param index any number
     *
     * @throws IllegalArgumentException if specified number is not a suitable index for storage array. Actually it
     *                                  means <br>{@code index < 0 || index >= capacity()}</br>
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= capacity()) {
            throw new IllegalArgumentException("Wrong index");
        }
    }

    /**
     * @param item type of item
     *
     * @return index of first occurrence of item of specified type
     */
    public synchronized int find(Item item) {
        return find(item, 0);
    }

    /**
     * @param item type of item
     * @param skip how much occurrences must be skipped
     *
     * @return index of {@code skip+1} occurrence of item of specified type
     */
    private int find(Item item, int skip) {
        if (item == null) {
            if (free() < 1) {
                return -1; //no empty space
            }
            for (int i = 0; i < capacity(); i++) {
                if (storage[i] == null && skip-- == 0) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < capacity(); i++) {
                if (item.equals(storage[i]) && skip-- == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Removes all items of specified type from storage
     *
     * @param item type of item
     *
     * @return type and total amount of removed items
     */
    public synchronized Item remove(Item item) {
        if (item == null) {return null;}
        Item tmp = new Item(item);
        for (int i : findAll(item)) {
            tmp.increase(remove(i).getAmount());
        }
        return tmp;
    }

    /**
     * Clears cell specified by index
     *
     * @param index index of cell
     *
     * @return removed items
     */
    public synchronized Item remove(int index) {
        checkIndex(index);
        Item tmp = storage[index];
        if (tmp == null) { return null; }
        storage[index] = null;
        occupied--;
        return tmp;
    }

    /**
     * Removes {@code amount} items of {@code item} type from storage
     *
     * @param item   type of item
     * @param amount how much items must be removed
     *
     * @return type and total amount of removed items
     *
     * @throws IllegalArgumentException if amount of stored items less than {@code amount}
     */
    public synchronized Item remove(Item item, int amount) {
        if (item == null) { return null; }
        int nominalAmount = getAmount(item);
        if (nominalAmount == amount) { return remove(item); }
        if (nominalAmount < amount) { throw new IllegalArgumentException("Not enough items");}

        Iterator<Integer> iter = findAll(item).iterator();
        while (amount > 0) {
            int index = iter.next();
            int localNominalAmount = getAmount(index);
            if (localNominalAmount > amount) {
                remove(index, amount);
                break;
            } else {
                remove(index);
                amount -= localNominalAmount;
            }
        }
        return new Item(item, amount);
    }

    /**
     * @param item type of items
     *
     * @return all occurrences of cells with items of specified type
     */
    public java.util.List<Integer> findAll(Item item) {
        java.util.List<Integer> indexes = new ArrayList<>();
        if (item == null) {
            for (int i = 0; i < capacity(); i++) {
                if (storage[i] == null) {
                    indexes.add(i);
                }
            }
        } else {
            for (int i = 0; i < capacity(); i++) {
                if (item.equals(storage[i])) {
                    indexes.add(i);
                }
            }
        }
        return indexes;
    }

    /**
     * Decreases amount of items in specified cell
     *
     * @param index  index of cell
     * @param amount amount of items to remove
     *
     * @return type and total amount of removed items
     */
    public synchronized Item remove(int index, int amount) {
        checkIndex(index);
        if (storage[index] == null) { return null; }
        if (storage[index].getAmount() == amount) {
            return remove(index);
        } else {
            Item divided = storage[index];
            divided.decrease(amount);
            return new Item(divided, amount);
        }
    }

    /**
     * @param item type of items
     *
     * @return {@code true} if at least one cell stores items of specified type
     */
    public synchronized boolean contains(Item item) {
        if (item == null) {
            return free() > 0;
        } else {
            for (Item i : storage) {
                if (item.equals(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Unites items of similar type from cells {@code from} and {@code to} into cell {@code to}
     *
     * @param from cell, where to get
     * @param to   cell, where to put the union
     */
    private synchronized void unite(int from, int to) {
        if (storage[to] == null) {
            if (storage[from] != null) {
                storage[to] = storage[from];
                storage[from] = null; //decrease(getAmount)
                occupied--;
            } // else both are null
        } else {
            if (storage[from] != null) {
                if (storage[from].equals(storage[to])) {
                    storage[to].increase(storage[from].getAmount());
                    storage[from] = null; //decrease(getAmount)
                    occupied--;
                }
            }
        }
    }

    public synchronized void move(Point p1, Point p2) {
        move(coordToCell(p1), coordToCell(p2));
    }

    /**
     * Swaps two cells.
     *
     * @param from first swapping cell
     * @param to   second swapping cell
     */
    public synchronized void move(int from, int to) {
        if (storage[to] != null) {
            if (storage[from] != null) {
                // non-null
                if (storage[from].equals(storage[to])) {
                    unite(from, to);
                } else {
                    storage[from].swap(storage[to]);
                }
            } else {
                // from -> null; to -> Item
                storage[from] = storage[to];
                storage[to] = null;
            }
        } else {
            // to -> null;
            if (storage[from] != null) {
                // from -> Item
                storage[to] = storage[from];
                storage[from] = null;
            }
        }
    }

    /**
     * Move some items from cell {@code from} to cell {@code to}.
     *
     * @param from   cell, where to get
     * @param to     cell, where to put gathered items
     * @param amount how much items to transfer
     */
    public synchronized void split(int from, int to, int amount) {
        if (amount < 1) { throw new IllegalArgumentException("You must move more than one item"); }
        if (storage[from] != null) {
            if (storage[to] == null) {
                Item divided = storage[from];
                divided.decrease(amount);
                storage[to] = new Item(divided, amount);
            } else if (storage[from].equals(storage[to])) {
                storage[from].decrease(amount);
                storage[to].increase(amount);
                occupied++;
            }
        }
    }

    /**
     * @return maximum possible number of stored items
     */
    public int capacity() {
        return storage.length;
    }

    /**
     * @return amount of free cells in storage
     */
    public int free() {
        return capacity() - occupied;
    }

    /**
     * Returns amount of lines in inventory available for storing smth.
     * <p>
     * By default it's 3. Further in game player will be able to get additional lines by some vendor NPC or by some
     * quest.
     *
     * @return amount of lines already achieved.
     */
    public int getSize() {
        return inventorySize;
    }

    /**
     * Collects significant information about inventory when painting module requested it.
     *
     * @return array which stores information about all stored items (and only about items: we don't care about
     * empty cells): coords, amount and id (to map image)
     */
    public int[][] toPaintableArrays() {
        int[][] arrays = new int[occupied][4];
        int cursor = 0;
        for (int i = 0; i < storage.length; i++) {
            if (storage[i] != null) {
                arrays[cursor][0] = i / LINE; //x
                arrays[cursor][1] = i % LINE; //y
                arrays[cursor][2] = storage[i].getID(); //image
                arrays[cursor][3] = storage[i].getAmount(); //nuff said
                cursor++;
            }
        }
        return arrays;
    }
}