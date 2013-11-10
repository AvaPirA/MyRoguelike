package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.game.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class AbstractGamePanel extends JPanel {

    static final         Font    logFont          = new Font("Times New Roman", Font.PLAIN, 15);
    private static final long    serialVersionUID = 1L;
    private static final Toolkit tKit             = Toolkit.getDefaultToolkit();
    private static final int SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT;
    private static final Color[] COLOR_SET = {Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
            Color.CYAN, Color.GRAY, Color.ORANGE, Color.YELLOW, Color.PINK};
    private static final String  path      = "res/sprite/";

    static Color getDefaultStringColor(final int index) {
        if (index < 0 || index > 9) {
            throw new IllegalArgumentException(
                    "Wrong default string color index: " + index + "while have only " + COLOR_SET.length + " color");
        }
        return COLOR_SET[index];
    }

    static {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_HEIGHT = (int) dim.getHeight();
        SCREEN_WIDTH = (int) dim.getWidth();
    }

    public static int getWidthInTiles() {
        return SCREEN_WIDTH / Tile.SIZE_px - 1 - 10;
    }

    public static int getHeightInTiles() {
        return SCREEN_HEIGHT / Tile.SIZE_px - 1;
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
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        // boolean hasAlpha = hasAlpha(image);
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            final int transparency = Transparency.OPAQUE;
            /*
             * if (hasAlpha) {
			 *
			 * transparency = Transparency.BITMASK;
			 *
			 * }
			 */
            // Create the buffered image
            final GraphicsDevice gs = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (final HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            final int type = BufferedImage.TYPE_INT_RGB;
            // int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
            /*
             * if (hasAlpha) {
			 *
			 * type = BufferedImage.TYPE_INT_ARGB;
			 *
			 * }
			 */
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

    void drawString(final Graphics2D g2, final int xx, final int yy, final String s) {
        g2.drawString(s, xx, yy + 8);
    }

    void drawImage(final Graphics g2, final Image img, final int xx, final int yy) {
        g2.drawImage(img, xx, yy, this);
    }

    final void paintBackground(final Graphics2D g2) {
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

    Image getImage(final String filename) {

        return tKit.getImage(path.concat(filename.endsWith(".png") ? filename : filename.concat(".png")));
    }

}
