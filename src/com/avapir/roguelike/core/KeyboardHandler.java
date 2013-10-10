package com.avapir.roguelike.core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler implements KeyListener {

	enum GameState {
		MOVE, VIEW, DISTANCE_ATTACK
	}

	private final Game		g;
	private final GameState	state;

	public KeyboardHandler(final Game game) {
		super();
		g = game;
		state = GameState.MOVE;
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		// TODO Auto- method stub

	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (g.isOver()) {
			afterGameOverPressings(e);
			return;
		}

		switch (state) {
		case DISTANCE_ATTACK:
		break;
		case MOVE:
			move(e);
		break;
		case VIEW:
		default:
			throw new IllegalStateException("Wrong game state");
		}
	}

	private void move(final KeyEvent e) {
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
		case KeyEvent.VK_END:
			p = new Point(0, 0);
		default:
			madeTurn = false;
			p = new Point(0, 0);
			g.EOT(p);
		break;
		}
		if (madeTurn) {
			final Point resultMove = g.getHero().move(p, g);
			if (resultMove != null) {
				g.EOT(resultMove);
			}
		}
	}

	private int	gameOverPressed	= 0;

	private void afterGameOverPressings(final KeyEvent e) {
		gameOverPressed++;
		if (gameOverPressed > 0) {
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
