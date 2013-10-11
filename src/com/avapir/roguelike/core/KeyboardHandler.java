package com.avapir.roguelike.core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.avapir.roguelike.core.Game.GameState;

public class KeyboardHandler implements KeyListener {

	private final Game	game;

	public KeyboardHandler(final Game game) {
		super();
		this.game = game;
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		switch (game.getState()) {
		case MOVE:
			moveType(e);
		break;
		case GAME_OVER:
			afterGameOverPressings(e);
		break;
		case CHANGE_STATS:
			changeStats();
		case VIEW:
			view(e);
		case DISTANCE_ATTACK:
			distanceAttack();
		case INVENTORY:
			inventory();
			RoguelikeMain.unimplemented();
		default:
			throw new IllegalStateException("Wrong game state");
		}
	}

	private void moveType(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'i':
			game.setGameState(GameState.INVENTORY);
		break;
		case 'd':
			game.setGameState(GameState.DISTANCE_ATTACK);
		break;
		case 'v':
			game.setGameState(GameState.VIEW);
		break;
		case 'c':
			game.setGameState(GameState.CHANGE_STATS);
		break;
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		switch (game.getState()) {
		case MOVE:
			movePress(e);
		break;
		case GAME_OVER:
			afterGameOverPressings(e);
		break;
		case CHANGE_STATS:
			changeStats();
		case VIEW:
			view(e);
		case DISTANCE_ATTACK:
			distanceAttack();
		case INVENTORY:
			inventory();
			RoguelikeMain.unimplemented();
		default:
			throw new IllegalStateException("Wrong game state");
		}
	}

	private void inventory() {
		// TODO Auto-generated method stub

	}

	private void distanceAttack() {
		// TODO Auto-generated method stub

	}

	private void changeStats() {
		// TODO Auto-generated method stub

	}

	private void view(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void movePress(final KeyEvent e) {
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
			return;
		}
		final Point resultMove = game.getHero().move(p, game);
		if (resultMove != null) {
			game.EOT(resultMove);
		}
	}

	private void afterGameOverPressings(final KeyEvent e) {
		System.exit(0);
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
