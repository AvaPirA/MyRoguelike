package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;

import java.awt.*;

public abstract class AbstractStateHandler implements StateHandler {

    final Game game;
    int y;
    private int x;

    AbstractStateHandler(final Game g) {
        game = g;
    }

    protected abstract int checkRestoreX(int x);

    protected abstract int checkRestoreY(int y);

    @Override
    public final void pressDown() {
        y++;
        y = checkRestoreY(y);
        game.repaint();
    }

    @Override
    public final void pressUp() {
        y--;
        y = checkRestoreY(y);
        game.repaint();
    }

    @Override
    public void pressLeft() {
        x--;
        x = checkRestoreX(x);
        game.repaint();
    }

    @Override
    public void pressRight() {
        x++;
        x = checkRestoreX(x);
        game.repaint();
    }

    @Override
    public Point getCursor() {
        return new Point(x, y);
    }

}
