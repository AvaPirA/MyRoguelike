package com.avapir.roguelike.game.world.map.fov;

import java.awt.*;

/**
 * An interface for FOV algorithms.
 *
 * @author sdatta
 */
public interface FovAlgorithm {

    /**
     * All locations of Board b that are visible from (x, y) will be visited, ie b.visit(x, y) will be called on them.
     * <p>
     * Algorithms must call visit on the same location only once. Algorithms should try to visit points closer to the
     * starting point before farther points. Algorithms should try to visit a location before calling isObstacle on it,
     * allowing effects like an explosion destroying a wall and affecting areas beyond it.
     *
     * @param b        The target board
     * @param distance How far can this Field of View go
     */
    public void visitFieldOfView(LosMap b, Point p, int distance);

}
