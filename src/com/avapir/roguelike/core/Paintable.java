package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;

import java.awt.*;

/**
 * User: Alpen Ditrix
 * Date: 14.03.14
 * Time: 18:00
 */
public interface Paintable {

    void paint(AbstractGamePanel panel, Graphics2D g2, int j, int i);
}
