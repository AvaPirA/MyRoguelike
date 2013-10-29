package com.avapir.roguelike.core;

import java.awt.Point;

public interface StateHandler {

	public abstract void pressDown();

	public abstract void pressUp();

	public abstract void pressLeft();

	public abstract void pressRight();

	public abstract Point getCursor();

}
