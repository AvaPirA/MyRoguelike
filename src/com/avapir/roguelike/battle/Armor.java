package com.avapir.roguelike.battle;

/**
 * Physical<br>
 * Magic<br>
 * Water<br>
 * Fire<br>
 * Lightning<br>
 * Curse
 */
public class Armor {

    public static final int TOTAL_DEF_TYPES = 6;

    private final float[] armor = new float[TOTAL_DEF_TYPES];

    public Armor(final float... input) {
        if (input.length > TOTAL_DEF_TYPES) {
            throw new IllegalArgumentException("Unknown armor type");
        }
        System.arraycopy(input, 0, armor, 0, input.length);
    }

    public float getArmor(final int index) {
        return armor[index];
    }

    public Armor addArmor(final int[] def) {
        if (def == null) {
            return this;
        }
        if (def.length != TOTAL_DEF_TYPES) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < TOTAL_DEF_TYPES; i++) {
            armor[i] += def[i];
        }
        return this;
    }

    public Armor addArmor(final Armor def) {
        if (def == null) {
            return this;
        }
        System.arraycopy(def.armor, 0, armor, 0, TOTAL_DEF_TYPES);
        return this;
    }

    public Armor replaceBy(final Armor armor) {
        if (armor == null) {
            return this;
        }
        System.arraycopy(armor.armor, 0, this.armor, 0, TOTAL_DEF_TYPES);
        return this;
    }

}
