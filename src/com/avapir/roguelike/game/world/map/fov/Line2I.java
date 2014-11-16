package com.avapir.roguelike.game.world.map.fov;

/**
 * A Eucleidian 2D line class represented by integers.
 *
 * @author Jonathan Duerig
 */
class Line2I {
    public Point2I near;
    public Point2I far;

    public Line2I(final Point2I newNear, final Point2I newFar) {
        near = newNear;
        far = newFar;
    }

    public boolean isBelow(final Point2I point) {
        return relativeSlope(point) > 0;
    }

    public boolean isBelowOrContains(final Point2I point) {
        return relativeSlope(point) >= 0;
    }

    public boolean isAbove(final Point2I point) {
        return relativeSlope(point) < 0;
    }

    public boolean isAboveOrContains(final Point2I point) {
        return relativeSlope(point) <= 0;
    }

    public boolean doesContain(final Point2I point) {
        return relativeSlope(point) == 0;
    }

    // negative if the line is above the point.
    // positive if the line is below the point.
    // 0 if the line is on the point.
    int relativeSlope(final Point2I point) {
        return (far.y - near.y) * (far.x - point.x) - (far.y - point.y) * (far.x - near.x);
    }

    @Override
    public String toString() {
        return "( " + near + " -> " + far + " )";
    }

}
