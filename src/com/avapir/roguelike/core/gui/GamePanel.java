package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.KeyboardHandler;
import com.avapir.roguelike.core.RoguelikeMain;
import com.avapir.roguelike.core.Viewport;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.game.ai.Borg;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Hero.PrimaryStats;
import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GamePanel extends AbstractGamePanel {

    private static final long serialVersionUID = 1L;
    private final Game       game;
    //    private final int        WIDTH_IN_TILES;
//    private final int        HEIGHT_IN_TILES;
    private final GuiPainter guiPainter;

    public GamePanel(final Game g) {
        super();

        game = g;
//        WIDTH_IN_TILES = getWidthInTiles();
//        HEIGHT_IN_TILES = getHeightInTiles();
        guiPainter = new GuiPainter();
        addKeyListener(new KeyboardHandler(game));
        setFocusable(true);
    }

    private class GuiPainter {

        private final Point attackOffset = new Point(0, 90);
        private final Point armorOffset  = new Point(150, 0);
        private final Point statsOffset  = new Point(-150, 110);
        private final Point o            = new Point(
                getWidthInTiles() * Tile.SIZE_px + 15, getHeightInTiles() * Tile.SIZE_px / 2);
        private final Point offsetDFT    = new Point(o.x, o.y);
        private final Font  defaultFont  = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        private int validMax;

        public void paint(final Graphics2D g2) {
            invalidateMax();
            final Hero h = game.getHero();
            o.move(offsetDFT.x, offsetDFT.y);
            heroInventory(g2, h);
            g2.setFont(defaultFont);
            heroMainStats(g2, h);
            heroAttack(g2, h);
            heroArmor(g2, h);
            heroStats(g2, h);
        }

        private void heroInventory(final Graphics2D g2, final Hero h) {
            final Point oI = new Point(o.x + 100, 100);// offset Inventory
            final Image itemBg = getImage("inventory_border");
            int itemBgWidth = itemBg.getWidth(null);
            int itemBgHeight = itemBg.getHeight(null);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    int xx = oI.x + i * (itemBgWidth + 1);
                    int yy = oI.y + j * (itemBgHeight + 1);
                    drawImage(g2, itemBg, xx, yy); //draw bg
                    Item item = h.getInventory().getWeared(i * 3 + j);
                    if (item != null) {
                        drawImage(g2, getImage(item.getImageName()), xx, yy); //draw item
                    }
                }
            }


            if (game.getState() == GameState.INVENTORY) {
                g2.setColor(Color.yellow);
                final Point cursor = game.getInventoryHandler().getCursor();
                g2.drawRect(
                        oI.x + cursor.x * (itemBg.getWidth(null) + 1),
                        oI.y + cursor.y * (itemBg.getHeight(null) + 1), itemBg.getWidth(null), itemBg.getHeight(null));
            }
        }

        private void heroMainStats(final Graphics2D g2, final Hero hero) {
            g2.setColor(Color.yellow);
            // location
            drawString(g2, o.x, o.y, "X: " + hero.getLoc().x);
            drawString(g2, o.x, o.y + 15, "Y: " + hero.getLoc().y);

            // name
            drawString(g2, o.x, o.y + 30, hero.getName());
            if (RoguelikeMain.BORG) {
                drawString(g2, o.x + 100, o.y + 30, ((Borg) hero.getAi()).getTargetString());
            }

            // level xp/XP
            drawString(g2, o.x +
                    80, o.y, String.format("Level [%s] (%s/%s)", hero.getLevel(), hero.getXP(), hero.getAdvanceXP()));

            // HP
            g2.setColor(Color.green);
            drawString(g2, o.x, o.y + 50, String.format("%s/%s", roundOneDigit(hero.getHP()), Hero.StatsFormulas
                                                                                                  .getMaxHP(hero)));

            // MP
            g2.setColor(Color.blue);
            drawString(g2, o.x, o.y + 65, String.format("%s/%s", roundOneDigit(hero.getMP()), Hero.StatsFormulas
                                                                                                  .getMaxMP(hero)));
        }

        private void heroAttack(final Graphics2D g2, final Hero hero) {
            g2.setColor(Color.red);

            o.translate(attackOffset.x, attackOffset.y);

            for (int i = 0; i < Attack.TOTAL_DMG_TYPES; i++) {
                final float heroDmg = roundOneDigit(hero.getAttack(i));
                drawString(g2, o.x, o.y + i * 15, Float.toString(heroDmg));
            }
        }

        private void heroArmor(final Graphics2D g2, final Hero hero) {
            g2.setColor(Color.orange);
            o.translate(armorOffset.x, armorOffset.y);

            for (int i = 0; i < Armor.TOTAL_DEF_TYPES; i++) {
                final float heroDef = roundOneDigit(hero.getArmor(i));
                drawString(g2, o.x, o.y + i * 15, Float.toString(heroDef));
            }
        }

        /**
         * 255\0->255\0 0-74 ::: case 0 255->0\255\0 75-149 ::: case 1 0\255\0->255 150-224 ::: case 2 0\255->0\255
         * 255-299 ::: case 3 300 ::: case 4
         *
         * @param stat
         *
         * @return
         */
        private Color getStatColor(final int stat) {
            if (stat < 0) {
                return Color.black;
            }

            int r = 0, g = 0, b = 0;
            final int factor = Hero.PrimaryStats.MAX_STAT_VALUE / 4;
            switch (stat / factor) {
                case 0:
                    r = 255;
                    g = 255 * stat / factor;
                    break;
                case 1:
                    r = 255 - 255 * (stat - factor) / factor;
                    g = 255;
                    break;
                case 2:
                    g = 255;
                    b = 255 * (stat - factor * 2) / factor;
                    break;
                case 3:
                    g = 255 - 255 * (stat - factor * 3) / factor;
                    b = 255;
                    break;
                case 4:
                    b = 255;
                    break;
                default: // stat > 300
                    r = 255;
                    b = 255;
            }
            return new Color(r, g, b, 64);
        }

        private int maxStat() {
            if (validMax == Integer.MIN_VALUE) {
                int max = 0;
                final List<Integer> arr = new ArrayList<>();
                for (int i = 0; i < Hero.PrimaryStats.PRIMARY_STATS_AMOUNT; i++) {
                    arr.add(Math.abs(game.getHero().getStats().values(i)));
                    if (game.getState() == GameState.CHANGE_STATS) {
                        arr.add(Math.abs(game.getHero().getStats().values(i) + game.getStatsHandler().getDiff()[i]));
                    }
                }

                for (final int i : arr) {
                    max = max > i ? max : i;
                }
                validMax = max;
            }
            return validMax;
        }

        private void invalidateMax() {
            validMax = Integer.MIN_VALUE;
        }

        private void heroStats(final Graphics2D g2, final Hero hero) {
            g2.setColor(Color.yellow);
            o.translate(statsOffset.x, statsOffset.y); // o == offset
            final int dFS = defaultFont.getSize(); // defaultFontSize
            final int oY = o.y - 4;// экспериментально полученное визуально лучшее значение
            final boolean isChangingStats = game.getState() == GameState.CHANGE_STATS;

            for (int i = 0; i < PrimaryStats.STATS_STRINGS.length; i++) {
                final int curStat = game.getHero().getStats().values(i);
                final int diff = isChangingStats ? game.getStatsHandler().getDiff()[i] : 0;

                // rectangles
                if (curStat + diff <= 0) {
                    g2.setColor(Color.black);
                    g2.fillRect(o.x, oY + i * dFS, 75 - 75 * (curStat + diff) / maxStat(), dFS);
                } else {
                    g2.setColor(getStatColor(curStat + diff));
                    g2.fillRect(o.x, oY + i * dFS, 75, dFS);
                    if (curStat > 0) {
                        g2.fillRect(o.x,
                                    oY + i * dFS, (int) (75 + Math.signum(curStat) * 75 * curStat / maxStat()), dFS);
                    }
                    g2.fillRect(o.x, oY + i * dFS, 75 + 75 * (curStat + diff) / maxStat(), dFS);
                }

                if (isChangingStats) {
                    // difference
                    g2.setColor(new Color(255, 255, 255, 128));
                    drawString(g2, o.x + 75, o.y + i * 15, (diff >= 0 ? "+" : "") + diff);
                }

                // "STR  239"
                g2.setColor(Color.yellow);
                drawString(g2, o.x,
                           o.y + i * 15, PrimaryStats.STATS_STRINGS[i] + getStatDelimeter(hero.getStats().values(i)) +
                        hero.getStats().values(i));

            }

            if (hero.getStats().hasFreeStats()) {
                int free = hero.getStats().getFree();
                if (isChangingStats) {
                    free += game.getStatsHandler().getFreeDiff();
                }
                drawString(g2, o.x, o.y + 6 * 15, "Не распределено: " + free);
            }

            if (game.getState() == GameState.CHANGE_STATS) {
                // cursor
                g2.setColor(Color.yellow);
                final int cursor = game.getStatsHandler().getCursor().y;
                game.getHero().getStats().values(cursor);

                g2.drawRect(o.x, oY + cursor * 15, 75 * 2, dFS);

            }
        }

        private String getStatDelimeter(final int stat) {
            if (stat < 0) {
                return getStatDelimeter(-stat).substring(1);// one space less cos we have '-'
            } else if (stat < 10) {
                return "    ";// 4
            } else if (stat < 100) {
                return "   ";// 3
            } else if (stat < 1000) {
                return "  ";// 2
            } else {
                return " ";// 1
            }
        }
    }

    void drawColorString(final Graphics g, final String str, int lastX, final int lastY) {
        final Graphics2D g2 = (Graphics2D) g;
        final FontRenderContext context = g2.getFontRenderContext();
        final Font f = new Font("Serif", Font.PLAIN, 12);
        g2.setFont(f);
        Rectangle2D bounds;
        final StringTokenizer st = new StringTokenizer(str, "#");
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("^")) {
                g2.setColor(Color.WHITE);
                continue;
            } else if (token.length() <= 2) {
                g2.setColor(getDefaultStringColor(Integer.parseInt(token)));
                continue;
            }
            drawString(g2, lastX, lastY, token);
            bounds = f.getStringBounds(token, context);
            lastX += bounds.getWidth();
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        // if (!BORG) {
        final Graphics2D g2 = (Graphics2D) g;
        paintMap(g2, game.getMap());
        paintLog(g2);

        drawDialogs(g);

        // } else {
        // TODO BORG RUN PAINTING
        // }
    }

    private void drawDialogs(Graphics g) {
        gameOverDialog(g);
    }

    private void gameOverDialog(Graphics g) {
        if (game.getState() == GameState.GAME_OVER) {
            final Image img = getImage("gameover");
            drawImage(g, img,
                      (getWidthInTiles() * Tile.SIZE_px - img.getWidth(null)) / 2,
                      (getHeightInTiles() * Tile.SIZE_px - img.getHeight(null)) / 4);
        }
    }

    @Override
    protected void paintGUI(final Graphics2D g2) {
        guiPainter.paint(g2);
    }

    private void paintLog(final Graphics2D g2) {
        final Point offset = new Point(15, 15);
        g2.setFont(logFont);
        for (int i = 0; i < game.getLog().size(); i++) {
            g2.setColor(Color.white);
            drawString(g2, offset.x, offset.y + i * logFont.getSize() + 3, game.getLog().get(i));
        }
    }

    private void debugShowMinimap(final Graphics2D g2, final Map map) {
        final int ox = 300;
        final int oy = 100;
        final int z = 15;

        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                Color c = Color.black;
                if (map.getTile(i, j).isPassable()) {
                    c = Color.yellow;
                    if (map.getTile(i, j).getMob() != null) {
                        final String name = map.getTile(i, j).getMob().getName();
                        if (name.equals("Slime")) {
                            c = Color.green;
                        } else if (name.equals("Hero")) {
                            c = Color.red;
                        }
                    }
                }
                g2.setColor(c);
                g2.fillRect(ox + z * i, oy + z * j, z, z);
            }
        }
    }

    private void paintMap(final Graphics2D g2, final Map map) {
//        g2.setFont(coordFont);
        final int ox = game.getCurrentX();
        final int oy = game.getCurrentY();
        for (int i = 0; i < getHeightInTiles(); i++) {
            for (int j = 0; j < getWidthInTiles(); j++) {

                // indexes on the Map
                final int x = ox - Viewport.HORIZONTAL_VIEW_LIMIT + j;
                final int y = oy - Viewport.VERTICAL_VIEW_LIMIT + i;
                // pixels where to paint Tile
                final int xx = j * Tile.SIZE_px;
                final int yy = i * Tile.SIZE_px;
                final Tile tile = map.getTile(x, y);
                g2.setColor(Color.red);
                if (map.hasTile(x, y)) {
                    drawImage(g2, getImage("empty"), xx, yy);
                    if (tile.isVisible()) {
                        paintTile(g2, tile, xx, yy);
//                        drawString(g2, xx, yy, String.format("%s,%s", x, y));
                    } else if (tile.isSeen()) {
                        paintTile(g2, tile, xx, yy);
                        drawImage(g2, getImage("wFog"), xx, yy);
                    } else {
                        // dS(g2, xx, yy, "invi");
                    }
                } else {
                    // dS(g2, xx, yy, "notile");
                }
            }
        }
    }

    private void paintTile(final Graphics2D g2, final Tile tile, final int xx, final int yy) {
        switch (tile.getType()) {
            case GRASS:
//                String s = "grass";
//                if (new Random().nextInt(10) > 8) {
//                    s = s.concat("_rot");
//                }
//                Image img = getImage(s);
//                g2.drawImage(img, xx, yy, img.getWidth(null) * 2, img.getHeight(null) * 2, null);
                drawImage(g2, getImage("grass"), xx, yy);
                break;
            case TREE:
                drawImage(g2, getImage("tree"), xx, yy);
                break;
            default:
                drawImage(g2, getImage("empty"), xx, yy);
                break;
        }

        if (tile.isVisible() && tile.getMob() != null)

        {
            paintMob(tile.getMob(), g2, xx, yy);
        }

    }

    private void paintMob(final Mob mob, final Graphics2D g2, final int xx, final int yy) {
        if (mob.isAlive()) {
            if (mob == game.getHero()) {
                drawImage(g2, getImage("hero"), xx, yy);
                paintColorBar(g2, xx, yy, Tile.SIZE_px, 3, mob.getHP() / mob.getMaxHp(), new Color(0, 255, 0, 128));
            } else { //if not a hero
                drawImage(g2, getImage(mob.getName().toLowerCase()), xx, yy);
                paintColorBar(g2, xx, yy, Tile.SIZE_px, 3, mob.getHP() / mob.getMaxHp(), new Color(255, 0, 0, 128));
            }
            //if hero or not
            paintColorBar(g2, xx, yy + 3, Tile.SIZE_px, 2, mob.getMP() / mob.getMaxMp(), new Color(0, 128, 255, 128));
        } else { //if dead
            if (mob == game.getHero()) {
                game.log("rip");
                drawImage(g2, getImage("rip"), xx, yy);
            }
        }
    }

    void paintColorBar(final Graphics2D g2,
                       final int x,
                       final int y,
                       final int width,
                       final int height,
                       final float value,
                       final Color transparentColor) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException();
        }
        g2.setColor(transparentColor);

        g2.fillRect(x, y, width, height);
        g2.fillRect(x, y, (int) (value * width), height);
    }

    // private boolean hasTileOnScreen(final int y, final int x) {
    // return y >= game.getCurrentY() && y < HEIGHT_IN_TILES + game.getCurrentY() && x >= game.getCurrentX()
    // && x < WIDTH_IN_TILES + game.getCurrentX();
    // }

}
