package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;

import java.awt.*;

public abstract class AbstractStateHandler implements StateHandler {

    protected int  y;
    protected int  x;


    protected abstract int checkRestoreX(int x);

    protected abstract int checkRestoreY(int y);

    @Override
    public final void pressDown() {
        y++;
        y = checkRestoreY(y);
        Game.getInstance().repaint();
    }

    @Override
    public final void pressUp() {
        y--;
        y = checkRestoreY(y);
        Game.getInstance().repaint();
    }

    @Override
    public void pressLeft() {
        x--;
        x = checkRestoreX(x);
        Game.getInstance().repaint();
    }

    @Override
    public void pressRight() {
        x++;
        x = checkRestoreX(x);
        Game.getInstance().repaint();
    }

    @Override
    public Point getCursor() {
        return new Point(x, y);
    }

}
