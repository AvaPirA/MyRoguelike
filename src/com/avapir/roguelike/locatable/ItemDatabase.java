package com.avapir.roguelike.locatable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Alpen Ditrix Date: 27.01.14 Time: 20:41
 */
public class ItemDatabase {

    private static final Map<Integer, ItemData> data = new HashMap<>();

    public static void init() {
        //todo
    }

    public static ItemData get(int ID) {
        return data.get(ID);
    }

}
