package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.avapir.roguelike.game.world.items.Item;

import java.awt.*;

/**
 * This is representation of hero's clothing, that contains: helm, vest, gloves, leggins, boots, necklace,
 * 2 weapons (or one two-handed), 2 rings, 2 artifacts. Also this class can compute all secondary stats which
 * equipped items provide.
 */
public final class EquipmentHandler {
    //todo maybe this class must be moved into InventoryHandler as inner(non-static nested) class?

    /** Total amount of places where some clothing may be put */
    private static final int SLOTS = 3 * 4;

    /**
     * art1  helm  art2
     * neck  vest  wep2
     * wep1  legg  glov
     * rng1  boot  rng2
     */
    private final Item[] equip = new Item[SLOTS];

    /** Inventory instance from where hero gets items to wear */
    private final InventoryHandler inventory;
    /** Cached attack value */
    private       Attack           attack;
    /** Cached armor value */
    private       Armor            armor;
    /** Cached weight value */
    private       int              weight;

    public EquipmentHandler(InventoryHandler inventory) {
        this.inventory = inventory;
    }

    /**
     * @param index slot index
     *
     * @return item dressed in specified slot
     */
    public Item get(int index) {
        if (index < 0 || index >= SLOTS) {throw new IllegalArgumentException("Wrong slot number");}
        return equip[index];
    }

    public Attack getAttack() {
        return attack;
    }

    public Armor getArmor() {
        return armor;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Update cache values when some item was put on or taken off
     */
    private void onChangeEquipment() {
        attack = new Attack();
        armor = new Armor();
        weight = 0;
        for (Item i : equip) {
            if (i != null) {
                attack.addAttack(i.getData().getAttack());
                armor.addArmor(i.getData().getArmor());
                weight += i.getData().getWeight();
            }
        }
    }

    /**
     * Puts one item from inventory to specified slot into equipment
     *
     * @param p    from where to get item
     * @param slot where to put item
     */
    public void putOn(Point p, ClothingSlots slot) {
        putOn(InventoryHandler.coordToCell(p), slot);
    }

    /**
     * Puts one item from inventory to specified slot into equipment
     *
     * @param index from where to get item
     * @param slot  where to put item
     */
    private void putOn(int index, ClothingSlots slot) {
        Item stored = inventory.get(index);
        if (stored == null) {return;}
        int i = slot.ordinal();
        if (equip[i] != null) {
            inventory.remove(index).swap(equip[i]);
        } else {
            equip[i] = inventory.remove(index);
        }

        onChangeEquipment();
    }

    public Item get(ClothingSlots slot) {
        return equip[slot.ordinal()];
    }

    /**
     * @param slots specified slot
     *
     * @return amount of items dressed in specified slot (usable for e.g. arrows)
     */
    public int getAmount(ClothingSlots slots) {
        return equip[slots.ordinal()].getAmount();
    }

    /**
     * Takes off item from specified slot of equipment to inventory. If inventory if full nothing will happen.
     *
     * @param slot specified slot
     *
     * @return item that was taken off
     */
    public Item takeOff(ClothingSlots slot) {
        if (inventory.free() > 0) {
            int i = slot.ordinal();
            Item tmp = equip[i];
            equip[i] = null;
            inventory.put(tmp);
            onChangeEquipment();
            return tmp;
        } else {
            Log.g("Can't take off due full inventory");
            return null;
        }
    }

}
