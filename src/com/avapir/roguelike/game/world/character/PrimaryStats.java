package com.avapir.roguelike.game.world.character;

/**
 * Here is the main hero's stats. They are used to compute formulas from {@link StatsFormulas}. Each time hero
 * reaches next level, he gains few "free stats" which can be spend on purchase any primary stat.
 */
public final class PrimaryStats {

    /**
     * That class describes default stats which are applied to every new instance of corresponding Hero
     */
    private static final class DefaultStats {
        /* 	STR 	AGI 	VIT 	INT 	DEX 	LUK */
        private static final int[] PLAYER = {3, 3, 3, 3, 2, 1};    // 16
        private static final int[] NPC    = {50, 100, 100, 50, 50, 10};  // 360
        private static final int[] ELDER  = {290, 120, 390, 700, 400, 100};    // 2000
        private static final int[] UNDEAD = {120, 40, 120, 0, 40, 0};    // 320

//        private static final int[] PLAYER = {300, 300, 300, 300, 300, 300}; // 887 test values
    }

    /** STRength <br> AGIlity <br> VITality <br> INTelligence <br> DEXterity <br> LUcK */
    public static final  String[] STATS_STRINGS         = {"STR", "AGI", "VIT", "INT", "DEX", "LUK"};
    /** Total amount of stats */
    public static final  int      PRIMARY_STATS_AMOUNT  = STATS_STRINGS.length;
    /** Maximum value which can have any stat */
    public static final  int      MAX_STAT_VALUE        = 300;
    /** Amount of "free stats" gained on each level up */
    private static final int      DEFAULT_STAT_INCREASE = 5;
    /** Stats storage */
    private final        int[]    values                = new int[PRIMARY_STATS_AMOUNT];
    /** Amount of available "free stats". Default value is amount of "free stats" available for new Hero */
    private              int      freeStats             = 100000;

    /**
     * Creates new stats instance for corresponding hero type
     *
     * @param name displayable hero name
     */
    public PrimaryStats(final String name) {
        // TODO name prefix recognition == crap
        int[] defaultStats;
        if (name.contains("NPC")) {
            defaultStats = DefaultStats.NPC;
        } else if (name.contains("Elder")) {
            defaultStats = DefaultStats.ELDER;
        } else if (name.contains("Undead")) {
            defaultStats = DefaultStats.UNDEAD;
        } else {
            defaultStats = DefaultStats.PLAYER;
        }
        System.arraycopy(defaultStats, 0, values, 0, PRIMARY_STATS_AMOUNT);
    }

    /**
     * Allows to iterate through stats array. Used in painting GUI.
     *
     * @param i index of requested stat
     *
     * @return requested stat value
     *
     * @throws java.lang.ArrayIndexOutOfBoundsException if {@code i < 0} or {@code i > }
     *                                                  {@link #PRIMARY_STATS_AMOUNT}
     */
    public int values(final int i) {return values[i];}

    public int getStr() {return values[0];}

    public int getAgi() {return values[1];}

    public int getVit() {return values[2];}

    public int getInt() {return values[3];}

    public int getDex() {return values[4];}

    public int getLuk() {return values[5];}

    /**
     * @param i stat index
     *
     * @return {@code true} if that stat can not be increased further
     */
    public boolean isMaxed(final int i) {
        return values[i] >= MAX_STAT_VALUE;
    }

    /**
     * Changes specified stat by specified value
     *
     * @param cursor index of stat
     * @param value  will be added to specified stat. May be negative
     */
    public void changeStatBy(final int cursor, final int value) {
        values[cursor] += value;
    }

    /**
     * @return {@code true} if Hero has at least one "free stat"
     */
    public boolean isLearnable() {
        return freeStats > 0;
    }

    public int getFreeStats() {
        return freeStats;
    }

    /**
     * Changes amount of "free stats" by specified value
     *
     * @param freeDiff will be added to {@link #freeStats}. May be negative
     */
    public void changeFreeBy(final int freeDiff) {
        freeStats += freeDiff;
    }

    public void defaultIncrease() {
        freeStats += DEFAULT_STAT_INCREASE;
    }
}