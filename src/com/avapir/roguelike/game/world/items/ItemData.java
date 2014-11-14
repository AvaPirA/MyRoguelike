package com.avapir.roguelike.game.world.items;

import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.sun.istack.internal.NotNull;

public class ItemData implements Cloneable {

    private final String name;
    private final Attack attack;
    private final Armor  armor;
    private final int    weight;
    private final int    stackSize;
    private       String imageName;

    private ItemData(String name, int weight, @NotNull Attack attack, @NotNull Armor armor, int stackSize) {
        this.name = name;
        imageName = "item_".concat(name.toLowerCase().replace(' ', '_'));
        this.attack = attack;
        this.armor = armor;
        this.weight = weight;
        this.stackSize = stackSize;

    }

    public static final class ItemBuilder {

        /**
         * For typical quest items
         *
         * @param name name of item
         *
         * @return created item
         */
        public static ItemData createItem(String name) {
            return createItem(name, 0);
        }

        /**
         * For typical consumables and resources
         *
         * @param name   name of item
         * @param weight weight of item
         *
         * @return created item
         */
        public static ItemData createItem(String name, int weight) {
            return createItem(name, weight, null, null, 1000);
        }

        /**
         * For typical weapons
         *
         * @param name   name of item
         * @param weight weight of item
         * @param attack attack power of item
         *
         * @return created item
         */
        public static ItemData createItem(String name, int weight, Attack attack) {
            return createItem(name, weight, attack, null, 1);
        }


        /**
         * For typical armor
         *
         * @param name   name of item
         * @param weight weight of item
         * @param armor  armor power of item
         *
         * @return created item
         */
        public static ItemData createItem(String name, int weight, Armor armor) {
            return createItem(name, weight, null, armor, 1);
        }

        /**
         * For everything
         *
         * @param name      name of item
         * @param weight    weight of item
         * @param attack    attack power of item
         * @param armor     armor power of item
         * @param stackSize maximum amount of items into one inventory cell
         *
         * @return created item
         */
        public static ItemData createItem(String name, int weight, Attack attack, Armor armor, int stackSize) {
            return new ItemData(name, weight, attack, armor, stackSize);
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
    public Object clone() throws CloneNotSupportedException {
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

