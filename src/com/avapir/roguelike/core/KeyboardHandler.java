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
			changeStatsType(e);
		break;
		case VIEW:
			viewType(e);
		break;
		case DISTANCE_ATTACK:
			distanceAttackType(e);
		break;
		case INVENTORY:
			inventoryType(e);
		break;
		default:
			throw new IllegalStateException("Wrong game state");
		}
		game.repaint();
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		switch (game.getState()) {
		case MOVE:
			movePress(e);
		break;
		case GAME_OVER:
			afterGameOverPressings(e);
		break;
		case CHANGE_STATS:
			changeStatsPress(e);
		break;
		case VIEW:
			viewPress(e);
		break;
		case DISTANCE_ATTACK:
			distanceAttackPress(e);
		break;
		case INVENTORY:
			inventoryPress(e);
		break;
		default:
			throw new IllegalStateException("Wrong game state: "+game.getState());
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
			game.createStatsHandler();
		break;
		}
	}

	private void inventoryType(KeyEvent e) {
		switch (e.getKeyChar()) {

		}
	}

	private void inventoryPress(KeyEvent e) {
		switch (e.getKeyCode()) {

		}
	}

	private void distanceAttackType(KeyEvent e) {
		switch (e.getKeyChar()) {

		}
	}

	private void distanceAttackPress(KeyEvent e) {
		switch (e.getKeyCode()) {

		}
	}

	private void changeStatsType(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'c':
			game.removeStatsHandler();
			game.setGameState(GameState.MOVE);
		}
	}

	private void changeStatsPress(KeyEvent e) {
		ChangingStatsHandler csh = game.getStatsHandler();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			csh.pressUp();
		break;
		case KeyEvent.VK_DOWN:
			csh.pressDown();
		break;
		case KeyEvent.VK_LEFT:
			csh.pressLeft();
		break;
		case KeyEvent.VK_RIGHT:
			csh.pressRight();
		break;
		}
	}

	private void viewType(KeyEvent e) {
		switch (e.getKeyChar()) {

		}
	}

	private void viewPress(KeyEvent e) {
		switch (e.getKeyCode()) {

		}
	}

	private void movePress(final KeyEvent e) {
		Point p;
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
