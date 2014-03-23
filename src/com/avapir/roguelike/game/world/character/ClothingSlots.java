package com.avapir.roguelike.game.world.character;

public enum ClothingSlots {
    ARTEFACT_1, HELMET, ARTEFACT_2,
    NECKLACE, VEST, WEAPON_2,
    WEAPON_1, LEGGINGS, GLOVES,
    RING_1, BOOTS, RING_2;

    public static final int NOT_DRESSED = -69;

    public static ClothingSlots fromCoord(int x, int y) {
        return fromInt(y * 10 + x);
    }

    public static final ClothingSlots fromInt(int i) {
        switch (i) {
            case 00:
                return ARTEFACT_1;
            case 01:
                return HELMET;
            case 02:
                return ARTEFACT_2;
            case 10:
                return NECKLACE;
            case 11:
                return VEST;
            case 12:
                return WEAPON_2;
            case 20:
                return WEAPON_1;
            case 21:
                return LEGGINGS;
            case 22:
                return GLOVES;
            case 30:
                return RING_1;
            case 31:
                return BOOTS;
            case 32:
                return RING_2;
            default:
                throw new IllegalArgumentException(String.format("There's no item at slot [%d.%d]", i / 10, i % 10));
        }
    }
}
