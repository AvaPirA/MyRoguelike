package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;

import java.awt.*;

public class Viewport {

    private Game game;

    public static final int HORIZONTAL_VIEW_LIMIT = AbstractGamePanel.getWidthInTiles() / 2;
    public static final int VERTICAL_VIEW_LIMIT   = AbstractGamePanel.getHeightInTiles() / 2;
    private int   horizontalDistance;
    private int   verticalDistance;
    private Point currentLocation;

    public Viewport(Point screenCenter, Game game) {
        this.game = game;
        horizontalDistance = AbstractGamePanel.getWidthInTiles() / 5;
        verticalDistance = AbstractGamePanel.getHeightInTiles() / 5;
        currentLocation = new Point(screenCenter.x, screenCenter.y);
    }

    public int getY() {
        return currentLocation.y;
    }

    public int getX() {
        return currentLocation.x;
    }

    public void move(Point p) {
        moveHorizontal(p.x);
        moveVertical(p.y);
    }


    private void moveHorizontal(int distX) {
        if (distX != 0 && goesOutOfHorizontal(distX)) {
            currentLocation.translate(distX, 0);
        }
    }

    private boolean goesOutOfHorizontal(int distX) {
        int heroX = game.getHero().getLoc().x;
        return Math.abs(heroX + distX - currentLocation.x) > horizontalDistance;
    }

    private boolean goesOutOfVertical(int distY) {
        int heroY = game.getHero().getLoc().y;
        return Math.abs(heroY + distY - currentLocation.y) > verticalDistance;
    }

    private void moveVertical(int distY) {
        if (distY != 0 && goesOutOfVertical(distY)) {
            currentLocation.translate(0, distY);
        }
    }

    public String toString() {
        return String.format("Viewport at %s. Box corners: \n%s    %s\n%s    %s", currentLocation, new Point(
                currentLocation.x - horizontalDistance, currentLocation.y - verticalDistance), new Point(
                currentLocation.x + horizontalDistance, currentLocation.y - verticalDistance), new Point(
                currentLocation.x - horizontalDistance, currentLocation.y + verticalDistance), new Point(
                currentLocation.x + horizontalDistance, currentLocation.y + verticalDistance));
    }
}
