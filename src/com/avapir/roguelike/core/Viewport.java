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
        System.out.println(horizontalDistance);
        System.out.println(verticalDistance);
        currentLocation = new Point(screenCenter.x, screenCenter.y);
    }

    public int getY() {
        return currentLocation.y;
    }

    public int getX() {
        return currentLocation.x;
    }

    public void move(Point p) {
        if (goesOutOfBox(p)) {
            currentLocation.translate(p.x, p.y);
        }
    }

    private boolean goesOutOfBox(Point dp) {
        Point newHeroLoc = new Point(game.getHero().getLoc());
        newHeroLoc.translate(dp.x, dp.y);
        return Math.abs(newHeroLoc.x - currentLocation.x) > horizontalDistance ||
                Math.abs(newHeroLoc.y - currentLocation.y) > verticalDistance;
    }

    public String toString() {
        return String.format("Viewport at %s. Box corners: \n%s    %s\n%s    %s", currentLocation, new Point(
                currentLocation.x - horizontalDistance, currentLocation.y - verticalDistance), new Point(
                currentLocation.x + horizontalDistance, currentLocation.y - verticalDistance), new Point(
                currentLocation.x - horizontalDistance, currentLocation.y + verticalDistance), new Point(
                currentLocation.x + horizontalDistance, currentLocation.y + verticalDistance));
    }
}
