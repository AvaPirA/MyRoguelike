package com.avapir.roguelike.core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto- method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		Game g = Game.getInstance();
		Map map = g.getCurrentMap();
		Point p;
		boolean madeTurn = true;;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			p = new Point(1, 0);
		case KeyEvent.VK_LEFT:
			p = new Point(-1, 0);
		case KeyEvent.VK_UP:
			p = new Point(0, 1);
		case KeyEvent.VK_DOWN:
			p = new Point(0, -1);
		default:
			madeTurn = false;
			p = new Point(0, 0);
		}
		if(madeTurn) {
			g.move(p);
			map.computeFOV(g.getHero().getX(), g.getHero().getY(), g.getHero().getHiddenStats().getFOVR());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
