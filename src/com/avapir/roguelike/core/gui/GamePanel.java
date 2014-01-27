package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.KeyboardHandler;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.core.Viewport;
import com.avapir.roguelike.game.ClothingSlots;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.locatable.DroppedItem;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Hero.PrimaryStats;
import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends AbstractGamePanel {

    private static final long serialVersionUID   = 1L;
    private static final int  STAT_BAR_HEIGHT_PX = 3;
    static final         Font logFont            = new Font("Times New Roman", Font.PLAIN, 15);
    static final         Font coordFont          = new Font("Monospaced", Font.PLAIN, 10);
    private final Game       game;
    private final GuiPainter guiPainter;

    public GamePanel(final Game g) {
        super();
        game = g;
        guiPainter = new GuiPainter();
        addKeyListener(new KeyboardHandler(game));
        setFocusable(true);
    }

    private class GuiPainter {
        private final Point o            = new Point(getMapWidth() + 15, getMapHeight() / 2);
        private final Point attackOffset = new Point(o.x, o.y + 90);
        private final Point armorOffset  = new Point(o.x + 150, o.y + 90);
        private final Point statsOffset  = new Point(o.x, o.y + 200);
        private final Point invenOffset  = new Point(o.x + 100, 100);
        private final Font  guiFont      = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        private int validMax;

        public void paint(final Graphics2D g2) {
            invalidateMax();
            final Hero h = game.getHero();
            heroInventory(h, g2);
            heroMainStats(h, g2);
            heroAttack(h, g2);
            heroArmor(h, g2);
            heroStats(h, g2);
        }

        private void heroInventory(final Hero h, final Graphics2D g2) {
            final Image itemBg = getImage("inventory_bg");
            int itemBgWidth = itemBg.getWidth(null);
            int itemBgHeight = itemBg.getHeight(null);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    int xx = invenOffset.x + i * (itemBgWidth + 1);
                    int yy = invenOffset.y + j * (itemBgHeight + 1);

                    g2.drawImage(itemBg, xx, yy, null);

                    Item item = h.getInventory().getDressed(ClothingSlots.fromInt(i * 3 + j));
                    if (item != null) {
                        g2.drawImage(getImage(item.getData().getImageName()), xx, yy, null);
                    }
                }
            }


            if (game.getState() == GameState.INVENTORY) {
                g2.setColor(Color.yellow);
                final Point cursor = game.getInventoryHandler().getCursor();
                g2.drawRect(
                        invenOffset.x + cursor.x * (itemBgWidth + 1),
                        invenOffset.y + cursor.y * (itemBgHeight + 1), itemBgWidth, itemBgHeight);
            }
        }

        private void heroMainStats(final Hero hero, final Graphics2D g2) {
            g2.setFont(guiFont);
            g2.setColor(Color.yellow);
            g2.drawString("X: " + hero.getLoc().x, o.x, o.y);
            g2.drawString("Y: " + hero.getLoc().y, o.x, o.y + 15);
            g2.drawString(hero.getName(), o.x, o.y + 30);
            g2.drawString(String.format("Level [%d] (%d/%d)", hero.getLevel(), hero.getXP(), hero.getAdvanceXP()),
                          o.x + 80, o.y);

            g2.setColor(Color.green);
            g2.drawString(String.format("%s/%s", roundOneDigit(hero.getHP()), Hero.StatsFormulas.getMaxHP(hero)), o.x,
                          o.y + 50);

            g2.setColor(Color.blue);
            g2.drawString(String.format("%s/%s", roundOneDigit(hero.getMP()), Hero.StatsFormulas.getMaxMP(hero)), o.x,
                          o.y + 65);
        }

        private void heroAttack(final Hero hero, final Graphics2D g2) {
            g2.setColor(Color.red);
            for (int i = 0; i < Attack.TOTAL_DMG_TYPES; i++) {
                final float heroDmg = roundOneDigit(hero.getAttack(i));
                g2.drawString(Float.toString(heroDmg), attackOffset.x, attackOffset.y + i * 15);
            }
        }

        private void heroArmor(final Hero hero, final Graphics2D g2) {
            g2.setColor(Color.orange);
            for (int i = 0; i < Armor.TOTAL_DEF_TYPES; i++) {
                final float heroDef = roundOneDigit(hero.getArmor(i));
                g2.drawString(Float.toString(heroDef), armorOffset.x, armorOffset.y + i * 15);
            }
        }

        /**
         * SP: 0-74 === case 0 === 255\0\0 -> 255\252\0
         * <p/>
         * SP: 75-149 === case 1 === 255\0\0 -> 0\255\0
         * <p/>
         * SP: 150-224 === case 2 === 0\255\0 -> 0\252\255
         * <p/>
         * SP: 225-299 === case 3 === 0\255\255 -> 0\3\255
         * <p/>
         * SP: 300 === case 4 === 0\0\255
         *
         * @param stat specified hero`s stat
         *
         * @return color from red to blue, through all spectre, which represents that stat-value from 0 to {@value
         * com.avapir.roguelike.locatable.Hero.PrimaryStats#MAX_STAT_VALUE}
         */
        private Color getStatToColor(final int stat) {
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

        private void heroStats(final Hero hero, final Graphics2D g2) {
            g2.setColor(Color.yellow);
            final int lineHeight = guiFont.getSize();
            final int oY = statsOffset.y - lineHeight + 3;// экспериментально полученное визуально лучшее значение
            final boolean isChangingStats = game.getState() == GameState.CHANGE_STATS;

            for (int i = 0; i < PrimaryStats.STATS_STRINGS.length; i++) {
                final int curStat = game.getHero().getStats().values(i);
                final int diff = isChangingStats ? game.getStatsHandler().getDiff()[i] : 0;

                // rectangles
                if (curStat + diff <= 0) {
                    g2.setColor(Color.black);
                    g2.fillRect(statsOffset.x, oY + i * lineHeight, 75 - 75 * (curStat + diff) / maxStat(), lineHeight);
                } else {
                    g2.setColor(getStatToColor(curStat + diff));
                    g2.fillRect(statsOffset.x, oY + i * lineHeight, 75, lineHeight);
                    if (curStat > 0) {
                        g2.fillRect(statsOffset.x, oY + i * lineHeight, (int) (75 +
                                Math.signum(curStat) * 75 * curStat / maxStat()), lineHeight);
                    }
                    g2.fillRect(statsOffset.x, oY + i * lineHeight, 75 + 75 * (curStat + diff) / maxStat(), lineHeight);
                }

                if (isChangingStats) {
                    // difference
                    g2.setColor(new Color(255, 255, 255, 128));
                    g2.drawString((diff >= 0 ? "+" : "") + diff, statsOffset.x + 75, statsOffset.y + i * 15);
                }

                g2.setColor(Color.yellow);
                g2.drawString(PrimaryStats.STATS_STRINGS[i] + getStatDelimiter(hero.getStats().values(i)) +
                                      hero.getStats().values(i), statsOffset.x, statsOffset.y + i * 15);

            }

            if (hero.getStats().hasFreeStats()) {
                int free = hero.getStats().getFree();
                if (isChangingStats) {
                    free += game.getStatsHandler().getFreeDiff();
                }
                g2.drawString("Не распределено: " + free, statsOffset.x, statsOffset.y + 6 * 15);
            }

            if (game.getState() == GameState.CHANGE_STATS) {
                // cursor
                g2.setColor(Color.yellow);
                final int cursor = game.getStatsHandler().getCursor().y;
                game.getHero().getStats().values(cursor);

                g2.drawRect(statsOffset.x, oY + cursor * 15, 75 * 2, lineHeight);

            }
        }

        private String getStatDelimiter(final int stat) {
            if (stat < 0) {
                return getStatDelimiter(-stat).substring(1);// one space less cos we have '-'
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

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        paintMap(game.getMap(), g2);
        paintLog(g2);
        drawDialogs(g2);
        debugShowMiniMap(game.getMap(), g2);
    }

    private void drawDialogs(Graphics2D g2) {
        gameOverDialog(g2);
    }

    private void gameOverDialog(Graphics2D g2) {
        if (game.getState() == GameState.GAME_OVER) {
            final Image img = getImage("gameover");
            g2.drawImage(img,
                         (getWidthInTiles() * Tile.SIZE_px - img.getWidth(null)) / 2,
                         (getHeightInTiles() * Tile.SIZE_px - img.getHeight(null)) / 4, null);
        }
    }

    @Override
    protected void paintGUI(final Graphics2D g2) {
        guiPainter.paint(g2);
    }

    private void paintLog(final Graphics2D g2) {
        final Point offset = new Point(15, 15);
        g2.setFont(logFont);
        g2.setColor(Color.white);
        for (int i = 0; i < Log.getSize(); i++)
            g2.drawString(Log.get(i), offset.x, offset.y + i * logFont.getSize() + 3);
    }

    private void debugShowMiniMap(final Map map, final Graphics2D g2) {
        final int z = 2;
        final int ox = z;
        final int oy = SCREEN_HEIGHT - map.getHeight() * z - 30;

        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                Color c = Color.black;
                Tile t = map.getTile(i, j);
                if (t.isPassable()) {
                    c = Color.yellow;
                    if (t.getMob() != null) {
                        final String name = t.getMob().getName();
                        if (name.equals("Slime")) {
                            c = Color.green;
                        } else if (name.equals("Hero")) {
                            c = Color.red;
                        }
                    }
                }
                if (!t.isVisible()) {
                    c = c.darker();
                }
                if (!t.isSeen()) {
                    c = c.darker();
                }
                g2.setColor(c);
                g2.fillRect(ox + z * i, oy + z * j, z, z);
            }
        }
    }

    private void paintMap(final Map map, final Graphics2D g2) {
        final int offsetX = game.getCurrentX();
        final int offsetY = game.getCurrentY();
        for (int i = 0; i < getHeightInTiles(); i++) {
            for (int j = 0; j < getWidthInTiles(); j++) {
                // indexes on the Map
                final int x = offsetX - Viewport.horizViewDistance() + j;
                final int y = offsetY - Viewport.verticalViewDistance() + i;
                final Tile tile = map.getTile(x, y);
                if (tile != null) {
                    paintTile(tile, j, i, g2);
                }
            }
        }

    }

    private void paintTile(final Tile tile, final int j, final int i, final Graphics2D g2) {
        drawToCell(g2, getImage("empty"), j, i);

        if (tile.isSeen()) {
            String imageName;
            switch (tile.getType()) {
                case GRASS:
                    imageName = "grass";
                    break;
                case TREE:
                    imageName = "tree";
                    break;
                default:
                    throw new RuntimeException("Unresolved tile type");
            }
            drawToCell(g2, getImage(imageName), j, i);

            if (tile.isVisible()) {
                if (tile.getMob() != null) {
                    paintMob(tile.getMob(), j, i, g2);
                }
                if (tile.getItemsAmount() > 0) {
                    paintItemsOnTile(tile.getItemList(), j, i, g2);
                }
            } else {
                if (tile.isSeen()) {
                    drawToCell(g2, getImage("wFog"), j, i);
                }
            }
        }
    }

    private void paintItemsOnTile(List<DroppedItem> itemList, int j, int i, Graphics2D g2) {
        drawToCell(g2, getImage(itemList.size() > 1 ? "many_items" : itemList.get(0).getItem().getData().getImageName()), j, i);
    }

    private void paintMob(final Mob mob, final int j, final int i, final Graphics2D g2) {
        if (mob.isAlive()) {
            if (mob == game.getHero()) {
                drawToCell(g2, getImage("hero"), j, i);
                paintColorBar(mob.getHP() / mob.getMaxHp(), new Color(0, 255, 0, 128), 0, j, i, g2);
            } else { //if not a hero
                drawToCell(g2, getImage(mob.getName().toLowerCase()), j, i);
                paintColorBar(mob.getHP() / mob.getMaxHp(), new Color(255, 0, 0, 128), 0, j, i, g2);
            }
            //if hero or not
            if (mob.getMaxMp() > 0) {
                paintColorBar(mob.getMP() / mob.getMaxMp(), new Color(0, 128, 255, 128), 1, j, i, g2);
            }
        } else { //if dead
            if (mob == game.getHero()) {
                drawToCell(g2, getImage("rip"), j, i);
            }
        }
    }

    void paintColorBar(final float value,
                       final Color transparentColor,
                       final int line,
                       final int j,
                       final int i,
                       final Graphics2D g2) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException();
        }
        g2.setColor(transparentColor);

        int x = j * Tile.SIZE_px;
        int y = i * Tile.SIZE_px + line * STAT_BAR_HEIGHT_PX;

        g2.fillRect(x, y, Tile.SIZE_px, STAT_BAR_HEIGHT_PX);
        g2.fillRect(x, y, (int) (value * Tile.SIZE_px), STAT_BAR_HEIGHT_PX);
    }
}
