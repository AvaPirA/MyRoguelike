package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.GameStateManager;
import com.avapir.roguelike.game.world.character.Hero;
import com.avapir.roguelike.game.world.character.PrimaryStats;

public class ChangingStatsHandler extends AbstractStateHandler {

    private final int[] diff;
    private       int   freeDiff;

    public ChangingStatsHandler() {
        super();
        diff = new int[PrimaryStats.PRIMARY_STATS_AMOUNT];
    }

    public int getFreeDiff() {
        return freeDiff;
    }

    private int free() {
        return freeDiff + Hero.getInstance().getStats().getFreeStats();
    }

    @Override
    public void pressRight() {
        if (free() > 0) {
            final int d = diff[y]++;
            diff[y] = checkRestoreX(diff[y]);
            if (d != diff[y]) {
                freeDiff--;
            }
            GameStateManager.getInstance().repaint();
        }
    }

    @Override
    public void pressLeft() {
        final int d = diff[y]--;
        diff[y] = checkRestoreX(diff[y]);
        if (d != diff[y]) {
            freeDiff++;
        }
        GameStateManager.getInstance().repaint();
    }

    @Override
    protected int checkRestoreX(final int x) {
        final int stat = Hero.getInstance().getStats().values(y);
        if (x + stat == 301) {
            return x - 1;
        } else {
            return x;
        }
    }

    @Override
    protected int checkRestoreY(final int y) {
        return y < 0 ? 0 : y > 5 ? 5 : y;
    }

    public void flush() {
        for (int i = 0; i < PrimaryStats.PRIMARY_STATS_AMOUNT; i++) {
            Hero.getInstance().getStats().changeStatBy(i, diff[i]);
        }
        Hero.getInstance().getStats().changeFreeBy(freeDiff);
    }

    public int[] getDiff() {
        return diff;
    }
}
