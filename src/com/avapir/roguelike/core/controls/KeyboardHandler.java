package com.avapir.roguelike.core.controls;

import com.avapir.roguelike.core.GameStateManager;
import com.avapir.roguelike.core.GameStateManager.GameState;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.core.Main;
import com.avapir.roguelike.core.Viewport;
import com.avapir.roguelike.core.statehandlers.StateHandler;
import com.avapir.roguelike.game.world.character.Hero;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardHandler extends KeyAdapter {

    private Point target = null;

    public KeyboardHandler() {
        super();
    }

    public void setBorgMove(final Point p) {
        target = p;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        switch (GameStateManager.getInstance().getState()) {
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
        GameStateManager.getInstance().repaint();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Main.exit();
        }
        switch (GameStateManager.getInstance().getState()) {
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
                throw new IllegalStateException("Wrong game state: " + GameStateManager.getInstance().getState());
        }
    }

    private void moveType(final KeyEvent e) {
        GameStateManager gsm = GameStateManager.getInstance();
        switch (e.getKeyChar()) {
            case 'i':
                gsm.setState(GameState.INVENTORY);
                gsm.createInventoryHandler();
                break;
            case 'd':
                gsm.setState(GameState.DISTANCE_ATTACK);
                break;
            case 'v':
                gsm.setState(GameState.VIEW);
                break;
            case 'c':
                gsm.setState(GameState.CHANGE_STATS);
                gsm.createStatsHandler();
                break;
        }
    }

    private void inventoryType(final KeyEvent e) {
        GameStateManager gsm = GameStateManager.getInstance();
        switch (e.getKeyChar()) {
            case 'e':
                gsm.getInventoryHandler().equip();
                break;
            case 'i':
                gsm.removeInventoryHandler();
                gsm.setState(GameState.MOVE);
        }
    }

    private void inventoryPress(final KeyEvent e) {
        GameStateManager gsm = GameStateManager.getInstance();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S:
                Log.g("Курсон переключен");
                gsm.getInventoryHandler().changeFocus();
                break;
            case KeyEvent.VK_ENTER:
                if (e.isShiftDown()) {
                    //ask about amount
                }
                gsm.getInventoryHandler().press();
                break;
            default:
                stateArrowsHandler(e.getKeyCode(), gsm.getInventoryHandler());
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
        GameStateManager gsm = GameStateManager.getInstance();
        switch (e.getKeyChar()) {
            case 'c':
                gsm.removeStatsHandler();
                gsm.setState(GameState.MOVE);
        }
    }

    private void changeStatsPress(final KeyEvent e) {
        stateArrowsHandler(e.getKeyCode(), GameStateManager.getInstance().getStatsHandler());
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
                GameStateManager.zoomIn();
                Viewport.getInstance().reset();
                Log.g("New zoom: %s%%", 100 * Tile.SIZE_px / 32f);
                return;
            case KeyEvent.VK_MINUS:
                if (Tile.SIZE_px > 1) {
                    GameStateManager.zoomOut();
                    Viewport.getInstance().reset();
                    Log.g("New zoom: %s%%", 100 * Tile.SIZE_px / 32f);
                }
                return;
            case KeyEvent.VK_NUMPAD5:
                Hero.getInstance().pickUpItems();
            default:
                return;
        }
        move(playerMove);
    }

    void move(final Point p) {
        if (target == null) {
            final Point resultMove = Hero.getInstance().move(p);
            if (resultMove != null) {
                GameStateManager.getInstance().EOT(resultMove);
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
