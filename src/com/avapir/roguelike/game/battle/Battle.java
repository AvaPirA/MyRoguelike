package com.avapir.roguelike.game.battle;

public class Battle {

    public static float computeDamage(final Attack atk, final Armor arm) {
        // TODO
        float totalDamage = 0;
        totalDamage += positive(atk.getDamageOfType(0) - arm.getArmorOfType(0));
        totalDamage += positive(atk.getDamageOfType(1) - arm.getArmorOfType(1));
        return totalDamage;
    }

    private static float positive(float f) {
        return f >= 0 ? f : 0;
    }

}
