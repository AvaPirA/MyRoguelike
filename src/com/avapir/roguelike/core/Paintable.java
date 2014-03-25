package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;

import java.awt.*;

/**
 * Every class which implements this interface may be painted on some {@link com.avapir.roguelike.core.gui
 * .AbstractGamePanel} using {@link java.awt.Graphics2D}
 */
public interface Paintable {

    /**
     * Draws object representation on some panel at specified context at specified coordinates
     *
     * @param panel panel where to draw representation of object
     * @param g2    drawing context
     * @param j     vertical pixel coordinate
     * @param i     horizontal pixel coorditate
     */
    void paint(AbstractGamePanel panel, Graphics2D g2, int j, int i);
}
