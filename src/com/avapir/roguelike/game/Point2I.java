package com.avapir.roguelike.game;

import java.awt.Point;
import java.io.Serializable;

/**
 * A class encapsulating a 2D point, as integers
 * 
 * (Reason for existance: java.awt.Point uses double
 * and I wanted speed.)
 * 
 * @author sdatta
 * 
 */
public class Point2I extends Point implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1360560915480181893L;

	public Point2I(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Point2I(final Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	/**
	 * Uses x+y as hash
	 */
	@Override
	public int hashCode() {
		return x << 7 - x + y;// x*prime+y
	}

	@Override
	public String toString() {
		return "Point2I[ " + x + ", " + y + " ]";
	}
}
