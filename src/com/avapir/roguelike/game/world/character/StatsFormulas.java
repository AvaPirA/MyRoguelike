package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.GameStateManager;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;

/**
 * Contains all formulas used for every secondary parameters: attack or defense power, hp and mp values,
 * FoV radius, endurance and so on. Also here is some sugar for primary hero stats, but that methods .
 */
public final class StatsFormulas {

    public static float getMaxHP(final Hero h) {
        final int baseHP = 2;
        final int STR = getStr(h);
        final int VIT = getVit(h);
        return baseHP + 4 * STR + 7 * VIT;
    }

    public static float getMaxMP(final Hero h) {
        final int baseMP = 1;
        final int INT = getInt(h);
        return baseMP + 7 * INT;
    }

    public static double addBonusXp(final Hero h, final double xp) {
        final int L = h.getStats().getLuk();
        final int D = h.getStats().getDex();
        // max XP is XP*(1+300/300+300/50) = 8*XP
        return (int) (xp * (1f + D / 300f + L / 50f));
    }

    public static int getFovRadius(final Hero h) {
        final int baseFOVR = 5;
        final int INT = getInt(h);
        return baseFOVR + INT / 50;
    }

    public static int getAtkRadius(final Hero h) {
        final int AGI = getAgi(h);
        return AGI == 0 ? 0 : AGI < 50 ? 1 : AGI < 150 ? 2 : 3;
    }

    public static Attack getAttack(final Hero h) {
        final float STR = getStr(h);
        final float DEX = getDex(h);
        final float INT = getInt(h);
        final float phy = 1.6f + STR + DEX * 0.4f + INT * 0.2f;
        final float mag = 1.2f + INT + DEX * 0.4f;
        return new Attack(phy, mag);
    }


    public static Armor getArmor(final Hero h) {
        final float STR = getStr(h);
        final float AGI = getAgi(h);
        final float INT = getInt(h);
        final float phy = AGI * 0.7f + STR * 0.3f;
        final float mag = INT * 0.5f;
        return new Armor(phy, mag);
    }

    public static boolean isOverweighted(Hero hero) {
        return hero.getEquipment().getWeight() > getMaxWeight(hero);
    }

    public static int getMaxWeight(Hero hero) {
        //fixme fix that out of the air formula
        return 30 * getStr(hero);
    }

    private static int getStr(final Hero h) {
        return getStat(h, 0);
    }

    private static int getAgi(final Hero h) {
        return getStat(h, 1);
    }

    private static int getVit(final Hero h) {
        return getStat(h, 2);
    }

    private static int getInt(final Hero h) {
        return getStat(h, 3);
    }

    private static int getDex(final Hero h) {
        return getStat(h, 4);
    }

    private static int getLuk(final Hero h) {
        return getStat(h, 5);
    }

    private static int getStat(final Hero h, final int i) {
        int STAT = h.getStats().values(i);
        final GameStateManager.GameState s = GameStateManager.getInstance().getState();
        if (s == GameStateManager.GameState.CHANGE_STATS) {
            STAT += GameStateManager.getInstance().getStatsHandler().getDiff()[i];
        }
        return STAT;
    }

    //todo new formulas

}
