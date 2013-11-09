package com.avapir.roguelike.core.statehandlers;

import java.awt.*;

public interface StateHandler {

    public abstract void pressDown();

    public abstract void pressUp();

    public abstract void pressLeft();

    public abstract void pressRight();

    public abstract Point getCursor();

}
