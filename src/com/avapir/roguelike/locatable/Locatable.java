package com.avapir.roguelike.locatable;

import java.awt.Point;

public interface Locatable {

	@Deprecated
	public int getX();

	@Deprecated
	public int getY();

	public Point getLoc();

	public void setLocation(final int x, final int y);

	public void setLocation(final Point p);

}
