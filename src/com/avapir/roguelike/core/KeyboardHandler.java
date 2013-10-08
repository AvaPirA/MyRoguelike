package com.avapir.roguelike.core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler implements KeyListener {

	private final Game	g;

	public KeyboardHandler(final Game game) {
		super();
		g = game;
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		// TODO Auto- method stub

	}

	@Override
	public void keyPressed(final KeyEvent e) {
		Point p;
		boolean madeTurn = true;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_NUMPAD1:
			p = new Point(-1, 1);
		break;
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_DOWN:
			p = new Point(0, 1);
		break;
		case KeyEvent.VK_NUMPAD3:
			p = new Point(1, 1);
		break;
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_LEFT:
			p = new Point(-1, 0);
		break;
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_RIGHT:
			p = new Point(1, 0);
		break;
		case KeyEvent.VK_NUMPAD7:
			p = new Point(-1, -1);
		break;
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_UP:
			p = new Point(0, -1);
		break;
		case KeyEvent.VK_NUMPAD9:
			p = new Point(1, -1);
		break;

		default:
			madeTurn = true;
			p = new Point(0, 0);
			g.EOT(p);
		break;
		}
		if (madeTurn) {
			Point resultMove = g.getHero().move(p, g);
			if (resultMove != null) {
				g.EOT(resultMove);
			}
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
