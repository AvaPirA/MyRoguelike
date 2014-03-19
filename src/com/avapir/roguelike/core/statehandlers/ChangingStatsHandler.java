package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Hero;

public class ChangingStatsHandler extends AbstractStateHandler {

    private final int[] diff;
    private       int   freeDiff;

    public ChangingStatsHandler(final Game g) {
        super(g);
        diff = new int[Hero.PrimaryStats.PRIMARY_STATS_AMOUNT];
    }

    public int getFreeDiff() {
        return freeDiff;
    }

    private int free() {
        return freeDiff + game.getHero().getStats().getFree();
    }

    @Override
    public void pressRight() {
        if (free() > 0) {
            final int d = diff[y]++;
            diff[y] = checkRestoreX(diff[y]);
            if (d != diff[y]) {
                freeDiff--;
            }
            game.repaint();
        }
    }

    @Override
    public void pressLeft() {
        final int d = diff[y]--;
        diff[y] = checkRestoreX(diff[y]);
        if (d != diff[y]) {
            freeDiff++;
        }
        game.repaint();
    }

    @Override
    protected int checkRestoreX(final int x) {
        final int stat = game.getHero().getStats().values(y);
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
        for (int i = 0; i < Hero.PrimaryStats.PRIMARY_STATS_AMOUNT; i++) {
            game.getHero().getStats().increaseBy(i, diff[i]);
        }
        game.getHero().getStats().changeFreeBy(freeDiff);
    }

    public int[] getDiff() {
        return diff;
    }
}
