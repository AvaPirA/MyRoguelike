package com.avapir.roguelike.core;

import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.statehandlers.StateHandler;
import com.avapir.roguelike.game.Tile;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardHandler extends KeyAdapter {

    private final Game game;
    private Point target = null;

    public KeyboardHandler(final Game game) {
        super();
        this.game = game;
        game.setKeyboardHandler(this);
    }

    public void setBorgMove(final Point p) {
        target = p;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
//        System.out.println(e);
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
//        System.out.println(e);
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
                throw new IllegalStateException("Wrong game state: " + game.getState());
        }
    }

    private void moveType(final KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'i':
                game.setState(GameState.INVENTORY);
                game.createInventoryHandler();
                break;
            case 'd':
                game.setState(GameState.DISTANCE_ATTACK);
                break;
            case 'v':
                game.setState(GameState.VIEW);
                break;
            case 'c':
                game.setState(GameState.CHANGE_STATS);
                game.createStatsHandler();
                break;
        }
    }

    private void inventoryType(final KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'i':
                game.removeInventoryHandler();
                game.setState(GameState.MOVE);
        }
    }

    private void inventoryPress(final KeyEvent e) {
        System.out.println(game.getInventoryHandler().isOnEquipment());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S:
                Log.g("Курсон переключен");
                game.getInventoryHandler().changeFocus();
                break;
            case KeyEvent.VK_ENTER:
                if (e.isShiftDown()) {
                    //ask about amount
                }
                game.getInventoryHandler().press();
                break;
            default:
                stateArrowsHandler(e.getKeyCode(), game.getInventoryHandler());
        }
    }

    private void distanceAttackType(final KeyEvent e) {
        switch (e.getKeyChar()) {

        }
    }

    private void distanceAttackPress(final KeyEvent e) {
        switch (e.getKeyCode()) {

        }
    }

    private void changeStatsType(final KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'c':
                game.removeStatsHandler();
                game.setState(GameState.MOVE);
        }
    }

    private void changeStatsPress(final KeyEvent e) {
        stateArrowsHandler(e.getKeyCode(), game.getStatsHandler());
    }

    private void viewType(final KeyEvent e) {
        switch (e.getKeyChar()) {

        }
    }

    private void viewPress(final KeyEvent e) {
        switch (e.getKeyCode()) {

        }
    }

    private void movePress(final KeyEvent e) {
        Point playerMove;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_NUMPAD1:
                playerMove = new Point(-1, 1);
                break;
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_DOWN:
                playerMove = new Point(0, 1);
                break;
            case KeyEvent.VK_NUMPAD3:
                playerMove = new Point(1, 1);
                break;
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_LEFT:
                playerMove = new Point(-1, 0);
                break;
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_RIGHT:
                playerMove = new Point(1, 0);
                break;
            case KeyEvent.VK_NUMPAD7:
                playerMove = new Point(-1, -1);
                break;
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_UP:
                playerMove = new Point(0, -1);
                break;
            case KeyEvent.VK_NUMPAD9:
                playerMove = new Point(1, -1);
                break;
            case KeyEvent.VK_END:
                playerMove = new Point(0, 0);
                break;
            case KeyEvent.VK_EQUALS:
                Game.zoomIn();
                game.resetViewport();
                Log.g("New zoom: %s%%", 100 * Tile.SIZE_px / 32f);
                return;
            case KeyEvent.VK_MINUS:
                if (Tile.SIZE_px > 1) {
                    Game.zoomOut();
                    game.resetViewport();
                    Log.g("New zoom: %s%%", 100 * Tile.SIZE_px / 32f);
                }
                return;
            case KeyEvent.VK_NUMPAD5:
                game.getHero().pickUpItems();
            default:
                return;
        }
        move(playerMove);
    }

    void move(final Point p) {
        if (target == null) {
            final Point resultMove = game.getHero().move(p, game);
            if (resultMove != null) {
                game.EOT(resultMove);
            }
        }
    }

    private void afterGameOverPressings(final KeyEvent e) {
        System.exit(0);
    }

    private void stateArrowsHandler(final int keyCode, final StateHandler sh) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                sh.pressUp();
                break;
            case KeyEvent.VK_DOWN:
                sh.pressDown();
                break;
            case KeyEvent.VK_LEFT:
                sh.pressLeft();
                break;
            case KeyEvent.VK_RIGHT:
                sh.pressRight();
                break;
        }
    }

}
