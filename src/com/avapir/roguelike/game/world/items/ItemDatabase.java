package com.avapir.roguelike.game.world.items;

import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Alpen Ditrix Date: 27.01.14 Time: 20:41
 */
public class ItemDatabase {

    private static final Map<Integer, ItemData> data = new HashMap<>();

    static {
        init();
    }
    public static void init() {
        //todo
        data.put(1, ItemData.ItemBuilder.createItem("Test sword", 1, new Attack(5, 5, 5, 5, 5, 5)));
        data.put(2, ItemData.ItemBuilder.createItem("Test helmet", 1, new Armor(1,1,1,1,1,1)));
        data.put(3, ItemData.ItemBuilder.createItem("Test vest", 1, new Armor(3, 3, 3, 3, 3, 3)));
        data.put(4, ItemData.ItemBuilder.createItem("Test trash", 1));
    }

    public static ItemData get(int ID) {
        return data.get(ID);
    }

    public static ItemData get(Item item) {
        return data.get(item.getID());
    }

}
