package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;

/**
 * @author Alpen
 */
public class Item {

    private static int items = 0;

    {
        items++;
    }

    private String name;
    private Attack attack;
    private Armor  armor;
    private int    weight;
    private boolean stackable;

    private Item(String name, int weight, Attack attack, Armor armor) {
        this.name = name;
        this.attack = attack == null ? new Attack() : attack;
        this.armor = armor == null ? new Armor() : armor;
        this.weight = weight;
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

    public static final class ItemBuilder {

        public static final Item createItem(String name) {
            return createItem(name, 0);
        }

        public static final Item createItem(String name, int weight) {
            return createItem(name, weight, null, null);
        }

        public static final Item craeteItem(String name, int weight, Attack attack) {
            return createItem(name, weight, attack, null);
        }

        public static final Item createItem(String name, int weight, Armor armor) {
            return createItem(name, weight, null, armor);
        }

        public static final Item createItem(String name, int weight, Attack attack, Armor armor) {
            return new Item(name, weight, attack, armor);
        }


    }

}
