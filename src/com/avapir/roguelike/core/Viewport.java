package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.world.Locatable;
import com.avapir.roguelike.game.world.character.Hero;

import java.awt.*;

public class Viewport {

    private static final Viewport INSTANCE = new Viewport(Locatable.UNRESOLVED_LOCATION);
    private Point currentLocation;

    private Viewport(Point screenCenter) {
        setCenter(screenCenter.x, screenCenter.y);
    }

    public static Viewport getInstance() {return INSTANCE;}

    public static int horizontalViewDistance() {
        return AbstractGamePanel.getWidthInTiles() / 2;
    }

    public static int verticalViewDistance() {
        return AbstractGamePanel.getHeightInTiles() / 2;
    }

    public static int horizontalBoxDistance() {
        return AbstractGamePanel.getWidthInTiles() / 5;
    }

    public static int verticalBoxDistance() {
        return AbstractGamePanel.getHeightInTiles() / 5;
    }

    public void setCenter(int x, int y) {
        currentLocation = new Point(x, y);
    }

    public void setCenter(Point p) {setCenter(p.x, p.y);}

    public int getX() {
        return currentLocation.x;
    }

    public int getY() {
        return currentLocation.y;
    }

    public void reset() {
        setCenter(Hero.getInstance().getLoc());
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

    private void moveVertical(int distY) {
        if (distY != 0 && goesOutOfVertical(distY)) {
            currentLocation.translate(0, distY);
        }
    }

    private boolean goesOutOfHorizontal(int distX) {
        int heroX = Hero.getInstance().getLoc().x;
        return Math.abs(heroX + distX - currentLocation.x) > horizontalBoxDistance();
    }

    private boolean goesOutOfVertical(int distY) {
        int heroY = Hero.getInstance().getLoc().y;
        return Math.abs(heroY + distY - currentLocation.y) > verticalBoxDistance();
    }

    public String toString() {
        return String.format("Viewport at %s. Box corners: \n%s    %s\n%s    %s", currentLocation,
                             new Point(currentLocation.x - horizontalBoxDistance(),
                                       currentLocation.y - verticalBoxDistance()),
                             new Point(currentLocation.x + horizontalBoxDistance(),
                                       currentLocation.y - verticalBoxDistance()),
                             new Point(currentLocation.x - horizontalBoxDistance(),
                                       currentLocation.y + verticalBoxDistance()),
                             new Point(currentLocation.x + horizontalBoxDistance(),
                                       currentLocation.y + verticalBoxDistance()));
    }
}
