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
		Game g = Game.getInstanceLast();
		Point p;
		boolean madeTurn = true;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			p = new Point(1, 0);
			break;
		case KeyEvent.VK_LEFT:
			p = new Point(-1, 0);
			break;
		case KeyEvent.VK_UP:
			p = new Point(0, -1);
			break;
		case KeyEvent.VK_DOWN:
			p = new Point(0, 1);
			break;
		default:
			madeTurn = false;
			p = new Point(0, 0);
			break;
		}
		if (madeTurn) {
			g.getHero().move(p);
			g.move(p);
			g.EOT();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
