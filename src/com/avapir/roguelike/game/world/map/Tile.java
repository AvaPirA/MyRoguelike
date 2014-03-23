package com.avapir.roguelike.game.world.map;

import com.avapir.roguelike.core.Paintable;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.DroppedItem;
import com.avapir.roguelike.game.world.items.Item;
import com.avapir.roguelike.game.world.items.ItemData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tile implements Paintable {

    public static enum Type {
        EMPTY, GRASS, TREE, CLOSED_DOOR, OPENED_DOOR, STAIR_UP, STAIR_DOWN, WALL;
        static final Tile[] examples = {getDefault(Type.EMPTY), getDefault(GRASS), getDefault(TREE)};

        static Tile getDefault(final Type t) {
            switch (t) {
                case EMPTY:
                    return new Tile(EMPTY, Flag.EMPTY, Flag.PASSABLE, Flag.TRANSPARENT);
                case GRASS:
                    return new Tile(GRASS, Flag.GRASS, Flag.PASSABLE, Flag.TRANSPARENT);
                case TREE:
                    return new Tile(TREE, Flag.GRASS);
                default:
                    return null;
            }
        }
    }

    public static int SIZE_px = 32;
    private final Tile.Type initialType;
    private Mob charHere = null;
    private List<DroppedItem> itemsHere;
    private int               flags;
    private boolean           visible;
    private boolean           seen;

    public Tile(final Tile.Type it) {
        initialType = it;
        flags = Tile.Type.examples[it.ordinal()].flags;
    }

    private Tile(final Tile.Type t, final int... flg) {
        initialType = t;
        for (final int f : flg) {
            flags |= f;
        }
    }

    public static final class Flag {

        public static final int FULL_FLAG     = 0b11111111111111111111111111111111;
        public static final int EMPTY_FLAG    = 0b00000000000000000000000000000000;
        /* 0-8 FOV */
        // public static final int VISIBLE = 1 << 0;
        // public static final int SEEN = 1 << 1;
        public static final int LIGHT_ON      = 1 << 2;
        public static final int TRANSPARENT   = 1 << 3;
        public static final int F4            = 1 << 4;
        public static final int F5            = 1 << 5;
        public static final int F6            = 1 << 6;
        public static final int F7            = 1 << 7;
        public static final int F8            = 1 << 8;
        /* 9-14 terrain and moving */
        public static final int PASSABLE      = 1 << 9;
        public static final int EMPTY         = 1 << 10;
        public static final int GRASS         = 1 << 11;
        public static final int STONES        = 1 << 12;
        public static final int ICE           = 1 << 13;
        /* 14-24 gaining effects */
        public static final int POISONING     = 1 << 14;
        public static final int FLAMING       = 1 << 15;
        public static final int WET           = 1 << 16;
        public static final int INSTANT_DEATH = 1 << 17;
        public static final int F18           = 1 << 18;
        public static final int F19           = 1 << 19;
        public static final int F20           = 1 << 20;
        public static final int F21           = 1 << 21;
        public static final int F22           = 1 << 22;
        public static final int F23           = 1 << 23;
        /* 24-31 specials */
        public static final int F24           = 1 << 24;
        public static final int F25           = 1 << 25;
        public static final int F26           = 1 << 26;
        public static final int F27           = 1 << 27;
        public static final int OPEN_DOOR     = 1 << 28;
        public static final int CLOSED_DOOR   = 1 << 29;
        public static final int UP_LADDER     = 1 << 30;
        public static final int DOWN_LADDER   = 1 << 31;

    }

    // private void addFlags(final int newFlag) {
    // flags = flags | newFlag;
    // }
    //
    // private void removeFlags(final int newFlag) {
    // flags = flags & invertFlag(newFlag);
    // }

    // private int invertFlag(final int flag) {
    // return Flag.FULL_FLAG ^ flag;
    // }

    // private void setFlag(final boolean b, final int flag) {
    // if (b) {
    // addFlags(flag);
    // } else {
    // removeFlags(flag);
    // }
    // }

    public void restoreDefault() {
        if (initialType != null) {
            flags = Tile.Type.examples[initialType.ordinal()].flags;
        }
    }

    private boolean checkFlag(final int flag) {
        return flag == (flags & flag);
    }

    public boolean isVisible() {return visible;}

    public void setVisible(final boolean b) {visible = b;}

    public boolean isSeen() {return seen;}

    public void setSeen(final boolean b) {seen = b;}

    public boolean isLantern() {return checkFlag(Flag.LIGHT_ON);}

    public boolean isTransparent() {return checkFlag(Flag.TRANSPARENT);}

    public boolean isPassable() {return checkFlag(Flag.PASSABLE);}

    public boolean isEmpty() {return checkFlag(Flag.EMPTY);}

    public boolean isGrass() {return checkFlag(Flag.GRASS);}

    public boolean isStone() {return checkFlag(Flag.STONES);}

    public boolean isIce() {return checkFlag(Flag.ICE);}

    public boolean isPoisoning() {return checkFlag(Flag.POISONING);}

    public boolean isFlaming() {return checkFlag(Flag.FLAMING);}

    public boolean isWet() {return checkFlag(Flag.WET);}

    boolean isInstantKiller() {return checkFlag(Flag.INSTANT_DEATH);}

    public boolean isClosed() {return checkFlag(Flag.CLOSED_DOOR);}

    public boolean isUpLadder() {return checkFlag(Flag.UP_LADDER);}

    public boolean isDownLadder() {return checkFlag(Flag.DOWN_LADDER);}

    public boolean putCharacter(final Mob chr) {
        if (charHere != null || !isPassable() || isInstantKiller()) {
            return false;
        } else {
            charHere = chr;
            return true;
        }
    }

    public void dropItem(final DroppedItem item) {
        if (itemsHere != null) {
            itemsHere.add(item);
        } else {
            itemsHere = new ArrayList<>();
            itemsHere.add(item);
        }
    }

    public void dropItems(final List<DroppedItem> items) {
        if (itemsHere != null) {
            itemsHere.addAll(items);
        } else {
            itemsHere = new ArrayList<>();
            itemsHere.addAll(items);
        }
    }

    public Mob getMob() {
        return charHere;
    }

    public List<DroppedItem> getItemList() {
        return itemsHere;
    }

    public int getItemsAmount() {
        return itemsHere == null ? 0 : itemsHere.size();
    }

    public Type getType() {
        return initialType;
    }

    public Mob removeCharacter() {
        final Mob c = charHere;
        charHere = null;
        return c;
    }

    @Override
    public void paint(AbstractGamePanel panel, Graphics2D g2, final int j, final int i) {
        panel.drawToCell(g2, panel.getImage("empty"), j, i);

        if (isSeen()) {
            String imageName;
            switch (getType()) {
                case GRASS:
                    imageName = "grass";
                    break;
                case TREE:
                    imageName = "tree";
                    break;
                default:
                    throw new RuntimeException("Unresolved tile type");
            }
            panel.drawToCell(g2, panel.getImage(imageName), j, i);

            if (isVisible()) {
                if (charHere != null) {
                    charHere.paint(panel, g2, j, i);
                }
                if (getItemsAmount() > 0) {
                    DroppedItem ditem = itemsHere.get(0);
                    Item item = ditem.getItem();
                    ItemData id = item.getData();
                    String name = id.getName();
                    panel.drawToCell(g2, panel.getImage(itemsHere.size() > 1 ? "many_items" : itemsHere.get(0)
                                                                                                       .getItem()
                                                                                                       .getData()
                                                                                                       .getImageName
                                                                                                               ()),
                                     j, i);
                }
            } else {
                if (isSeen()) {
                    panel.drawToCell(g2, panel.getImage("wFog"), j, i);
                }
            }
        }
    }
}
