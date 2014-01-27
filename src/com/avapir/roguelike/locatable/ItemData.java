package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;

public class ItemData implements Cloneable {

    private final String  name;
    private final Attack  attack;
    private final Armor   armor;
    private final int     weight;
    private final boolean stackable;
    private String imageName;

    private ItemData(String name, int weight, Attack attack, Armor armor) {
        this.name = name;
        imageName = "item_".concat(name.toLowerCase().replace(' ', '_'));
        this.attack = attack == null ? new Attack() : attack;
        this.armor = armor == null ? new Armor() : armor;
        this.weight = weight;
        stackable = false;

    }

    public static final class ItemBuilder {

        public static final ItemData createItem(String name) {
            return createItem(name, 0);
        }

        public static final ItemData createItem(String name, int weight) {
            return createItem(name, weight, null, null);
        }

        public static final ItemData createItem(String name, int weight, Attack attack) {
            return createItem(name, weight, attack, null);
        }

        public static final ItemData createItem(String name, int weight, Armor armor) {
            return createItem(name, weight, null, armor);
        }

        public static final ItemData createItem(String name, int weight, Attack attack, Armor armor) {
            return new ItemData(name, weight, attack, armor);
        }

    }

    public String getName() {
        return name;
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

    @Override
    public Object clone() {
        try {
            Item item = (Item) super.clone();
            //all fields are final and none of them will be changed in the item`s lifetime, so deep clone if not proper
            return item;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public String getImageName() {
        return imageName;
    }
}

