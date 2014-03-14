package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.game.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class AbstractGamePanel extends JPanel {

    private static final long    serialVersionUID = 1L;
    private static final Toolkit tKit             = Toolkit.getDefaultToolkit();
    protected static final int SCREEN_WIDTH;
    protected static final int SCREEN_HEIGHT;
    private static final Color[] COLOR_SET = {Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
            Color.CYAN, Color.GRAY, Color.ORANGE, Color.YELLOW, Color.PINK};
    private static final String  path      = "res/sprite/";

    static {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_HEIGHT = dim.height;
        SCREEN_WIDTH = dim.width;
    }

    protected static Color getDefaultStringColor(final int index) {
        if (index < 0 || index > 9) {
            throw new IllegalArgumentException(String.format(
                    "Wrong default string color index: %s while have only %s" + " colors", index, COLOR_SET.length));
        }
        return COLOR_SET[index];
    }

    public static int getMapWidth() {
        return SCREEN_WIDTH - 330;
    }

    public static int getWidthInTiles() {
        return getMapWidth() / Tile.SIZE_px;
    }

    public static int getMapHeight() {
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

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        /*
         * Determine if the image has transparent pixels; for this method's
         * implementation, see e661 Determining If an Image Has Transparent Pixels
         * boolean hasAlpha = hasAlpha(image);
         * Create a buffered image with a format that's compatible with the screen
         */
        BufferedImage bimage = null;

        // Create the buffered image
        try {
            final int transparency = Transparency.OPAQUE;
            final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                                .getDefaultScreenDevice()
                                                                .getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (final HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            final int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        final Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);
        paintGUI(g2);
    }

    public void drawToCell(final Graphics g2, final Image img, final int j, final int i) {
        g2.drawImage(img, j * Tile.SIZE_px, i * Tile.SIZE_px, Tile.SIZE_px, Tile.SIZE_px, this);
    }

    public void printToCell(final Graphics g2, final String str, final int j, final int i) {
        g2.drawString(str, j * Tile.SIZE_px, i * Tile.SIZE_px);
    }

    private void paintBackground(final Graphics2D g2) {
        final BufferedImage bgTex = toBufferedImage(getImage("background"));
        final Rectangle2D canvas = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        final Rectangle2D tr = new Rectangle2D.Double(0, 0, bgTex.getWidth(), bgTex.getHeight());

        // Create the TexturePaint.
        final TexturePaint tp = new TexturePaint(bgTex, tr);
        g2.setPaint(tp);
        g2.fill(canvas);
        // TODO draw borders of viewable area of map
    }

    protected abstract void paintGUI(final Graphics2D g2);

    /**
     *
     * @deprecated must be replaced with ImageResources
     * @param filename
     * @return
     */
    @Deprecated
    public Image getImage(final String filename) {
        String file = path.concat(filename.endsWith(".png") ? filename : filename.concat(".png"));
        return tKit.getImage(file);
    }
}
