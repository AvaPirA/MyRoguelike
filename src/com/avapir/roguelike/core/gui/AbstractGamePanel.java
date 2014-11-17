package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.core.resources.ImageResources;
import com.avapir.roguelike.game.world.map.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class AbstractGamePanel extends JPanel {

    static final int SCREEN_WIDTH;
    static final int SCREEN_HEIGHT;
    private static final long serialVersionUID = 1L;

    static {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_HEIGHT = dim.height;
        SCREEN_WIDTH = dim.width;
    }

    static int getMapWidth() {
        return SCREEN_WIDTH - 330;
    }

    public static int getWidthInTiles() {
        return getMapWidth() / Tile.SIZE_px;
    }

    static int getMapHeight() {
        return SCREEN_HEIGHT;
    }

    public static int getHeightInTiles() {
        return getMapHeight() / Tile.SIZE_px;
    }

    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public static float roundOneDigit(final float f) {
        return Math.round(10 * f) / 10f;
    }

    public static float roundThreeDigits(final float f) {
        return Math.round(1000 * f) / 1000f;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        paintBackground(g2);
        drawGUI(g2);
    }

    public void drawToCell(final Graphics g2, final Image img, final int j, final int i) {
        g2.drawImage(img, j * Tile.SIZE_px, i * Tile.SIZE_px, Tile.SIZE_px, Tile.SIZE_px, this);
    }

    void printToCell(final Graphics g2, final String str, final int j, final int i) {
        g2.drawString(str, j * Tile.SIZE_px, (i + 1) * Tile.SIZE_px);
    }

    private void paintBackground(final Graphics2D g2) {
//        final BufferedImage bgTex = toBufferedImage(getImage("background"));
        final BufferedImage bgTex = ImageResources.getImage("background");
        final Rectangle2D canvas = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        final Rectangle2D tr = new Rectangle2D.Double(0, 0, bgTex.getWidth(), bgTex.getHeight());

        // Create the TexturePaint.
        final TexturePaint tp = new TexturePaint(bgTex, tr);
        g2.setPaint(tp);
        g2.fill(canvas);
        // TODO draw borders of viewable area of map
    }

    protected abstract void drawGUI(final Graphics2D g2);

}
