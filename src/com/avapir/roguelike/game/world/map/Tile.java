package com.avapir.roguelike.game.world.map;

import com.avapir.roguelike.core.Paintable;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.DroppedItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Smallest discrete piece of the whole terrain on the {@link com.avapir.roguelike.game.world.map.Map}. There many
 * types
 * of tiles with unique appearance and functionality.
 *
 * @see com.avapir.roguelike.game.world.map.Tile.Type
 * @see com.avapir.roguelike.game.world.map.Tile.Flag
 */
public class Tile implements Paintable {

    /**
     * Here described all default types of tiles. Each one may be modified by some game triggers or situations.
     */
    public static enum Type {
        /**
         * Typical passable tile without any effects
         */
        EMPTY,
        /**
         * Typical "friendly" tile
         */
        GRASS,
        /**
         * Decorative impassible tile
         */
        TREE,
        /**
         * That tile is impassible by default. To transform it to {@link Tile.Type#OPENED_DOOR} you must find the key
         * or
         * do some trigger action. Mobs also can't open the door.
         */
        CLOSED_DOOR,
        /**
         * Opened door is passable by default but it can be closed after some trigger event.
         */
        OPENED_DOOR,
        /**
         * That tile sends hero (ineffective to mobs) to a higher level
         */
        STAIR_UP,
        /**
         * That tile sends hero (ineffective to mobs) to a lower level
         */
        STAIR_DOWN,
        /**
         * That tile is unpassable by default, but may be broken somehow. //todo
         */
        WALL;
    }

    /**
     * Here is set of default tiles (that allows not to create new object on demand)
     */
    private static final Tile[] defaultTiles = {new Tile(Type.EMPTY, Flag.EMPTY, Flag.PASSABLE, Flag.TRANSPARENT),
            new Tile(Type.GRASS, Flag.GRASS, Flag.PASSABLE, Flag.TRANSPARENT), new Tile(Type.TREE, Flag.GRASS)};

    /**
     * @param t type of required tile
     *
     * @return default tile of specified type
     */
    static Tile getDefault(final Type t) {
        switch (t) {
            case EMPTY:
                return defaultTiles[0];
            case GRASS:
                return defaultTiles[1];
            case TREE:
                return defaultTiles[3];
            default:
                return null;
        }
    }

    /**
     * Size of painting cell on the screen. Used by painter-class to apply zooming-effect
     */
    public static int SIZE_px = 32;
    /**
     * Initial type of tile. That allows to reset to initial state when tile was modified on some event.
     */
    private final Tile.Type initialType;
    /**
     * Character, which stays at this tile. It may be only one, so if some character want to stay on the engaged tile,
     * he must kill {@link Tile#charHere}
     */
    private Mob charHere = null;
    /**
     * All the items dropped on the tile. They may be dropped here after some mob's death or by some trigger event. All
     * items may be picked up by some mob (including hero)
     */
    private List<DroppedItem> itemsHere;
    /**
     * Storage of all tile's special properties
     *
     * @see com.avapir.roguelike.game.world.map.Tile.Flag
     */
    private int               flags;
    /**
     * While tile is visible, player can see most of it's properties
     */
    private boolean visible = false;
    /**
     * Tile becomes {@code seen == true} since it was visible first time. Also, tile may be visible by default for some
     * times. For seen, but non=visible tiles player can see only few it's properties
     */
    private boolean seen    = false;

    /**
     * Most used constructor
     *
     * @param it initial type of tile
     */
    public Tile(final Tile.Type it) {
        initialType = it;
        restoreDefault();
    }

    /**
     * This constructor used only for default tiles
     *
     * @param type  initial type of tile
     * @param flags array of properties
     *
     * @see Tile#defaultTiles
     * @see com.avapir.roguelike.game.world.map.Tile.Flag
     */
    private Tile(final Tile.Type type, final int... flags) {
        initialType = type;
        for (final int f : flags) {
            this.flags |= f;
        }
    }

    public static final class Flag {

        /**
         * Integer value which has all bytes equals 1.
         */
        public static final int FULL_FLAG = 0b11111111111111111111111111111111;

        /**
         * Integer value which has all bytes equals 1.
         */
        public static final int EMPTY_FLAG = 0b00000000000000000000000000000000;

        /* 0-8 FOV */
        // public static final int VISIBLE = 1 << 0;
        // public static final int SEEN = 1 << 1;
        /**
         * When such tile is visible, it wil enlarge sight area.
         */
        public static final int LIGHT_ON      = 1 << 2;
        /**
         * Allows "sight rays" go throwg this tile.
         */
        public static final int TRANSPARENT   = 1 << 3;
        public static final int F4            = 1 << 4;
        public static final int F5            = 1 << 5;
        public static final int F6            = 1 << 6;
        public static final int F7            = 1 << 7;
        public static final int F8            = 1 << 8;
        /* 9-14 terrain and moving */
        /**
         * Any mob can stay on such tiles
         */
        public static final int PASSABLE      = 1 << 9;
        /**
         * Tile without some special terrain properties. Just a ground. Or stone. Or sand.
         */
        public static final int EMPTY         = 1 << 10;
        /**
         * That's friendly tile. On each turn staying on that tile may randomly give you some buff. //todo grass
         * effects
         */
        public static final int GRASS         = 1 << 11;
        /**
         * Unfriendly tile. That's not typical stones -- that's almost impassible mound. On each turn you may randomly
         * stumble and get some debuff. //todo stones effects
         */
        public static final int STONES        = 1 << 12;
        /**
         * Unfriendly tile. //todo ice effects
         */
        public static final int ICE           = 1 << 13;
        /* 14-24 gaining effects */
        /**
         * Hostile tile. Applies Poisoning to any character staying here.
         */
        public static final int POISONING     = 1 << 14;
        /**
         * Hostile tile. Applies Flaming to any character staying here.
         */
        public static final int FLAMING       = 1 << 15;
        /**
         * Hostile tile. Applies Soaking to any character staying here.
         */
        public static final int WET           = 1 << 16;
        /**
         * Extremely hostile tile. Applies SoonDeath to any character staying here.
         */
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
        /**
         * Door that already opened
         */
        public static final int OPEN_DOOR     = 1 << 28;
        /**
         * Closed door
         */
        public static final int CLOSED_DOOR   = 1 << 29;
        /**
         * Teleports hero to a higher level
         */
        public static final int UP_LADDER     = 1 << 30;
        /**
         * Teleports hero to a lower level
         */
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

    /**
     * Sets tile properties according to it's {@link Tile#initialType}
     */
    public void restoreDefault() {
        if (initialType != null) {
            flags = getDefault(initialType).flags;
        }
    }

    /**
     * @param flag one-byte integer from {@link com.avapir.roguelike.game.world.map.Tile.Flag}
     *
     * @return true, if specified flag is "On"
     */
    private boolean checkFlag(final int flag) {
        return flag == (flags & flag);
    }

    /**
     * @return visibility flag
     */
    public boolean isVisible() {return visible;}

    /**
     * @param b new visibility flag value
     */
    public void setVisible(final boolean b) {visible = b;}

    /**
     * @return seenability flag
     */
    public boolean isSeen() {return seen;}

    /**
     * @param b new seenability flag value
     */
    public void setSeen(final boolean b) {seen = b;}

    /**
     * @return lighting flag
     */
    public boolean isLantern() {return checkFlag(Flag.LIGHT_ON);}

    /**
     * @return transparency flag
     */
    public boolean isTransparent() {return checkFlag(Flag.TRANSPARENT);}

    /**
     * @return passability flag
     */
    public boolean isPassable() {return checkFlag(Flag.PASSABLE);}

    public boolean isEmpty() {return checkFlag(Flag.EMPTY);}

    public boolean isGrass() {return checkFlag(Flag.GRASS);}

    public boolean isStone() {return checkFlag(Flag.STONES);}

    public boolean isIce() {return checkFlag(Flag.ICE);}

    public boolean isPoisoning() {return checkFlag(Flag.POISONING);}

    public boolean isFlaming() {return checkFlag(Flag.FLAMING);}

    public boolean isWet() {return checkFlag(Flag.WET);}

    /**
     * @return true if that's killer-tile
     */
    boolean isInstantKiller() {return checkFlag(Flag.INSTANT_DEATH);}

    /**
     * @return true, if that's door-tile and it's closed
     */
    public boolean isClosed() {return checkFlag(Flag.CLOSED_DOOR);}

    public boolean isUpLadder() {return checkFlag(Flag.UP_LADDER);}

    public boolean isDownLadder() {return checkFlag(Flag.DOWN_LADDER);}

    /**
     * Puts specified character to this tile, if possible
     * @param chr character instance
     * @return true if character was put
     */
    public boolean putCharacter(final Mob chr) {
        if (charHere != null || !isPassable() || isInstantKiller()) {
            return false;
        } else {
            charHere = chr;
            return true;
        }
    }

    /**
     * Puts one specified item to tile's storage.
     * @param item item reference
     */
    public void dropItem(final DroppedItem item) {
        if (itemsHere != null) {
            itemsHere.add(item);
        } else {
            itemsHere = new ArrayList<>();
            itemsHere.add(item);
        }
    }

    /**
     * Puts few specified items to tile's storage
     * @param items list of items
     */
    public void dropItems(final List<DroppedItem> items) {
        if (itemsHere != null) {
            itemsHere.addAll(items);
        } else {
            itemsHere = new ArrayList<>();
            itemsHere.addAll(items);
        }
    }

    /**
     * @return null or mob instance, if it's staying on this tile
     */
    public Mob getMob() {
        return charHere;
    }

    /**
     * @return list of items stored in this tile
     */
    public List<DroppedItem> getItemList() {
        return itemsHere;
    }

    /**
     * @return amount of stored items
     */
    public int getItemsAmount() {
        return itemsHere == null ? 0 : itemsHere.size();
    }

    /**
     * @return {@link com.avapir.roguelike.game.world.map.Tile.Type#initialType}
     */
    public Type getType() {
        return initialType;
    }

    /**
     * Removes character from this tile (but not deletes that object -- it's also stored in {@link com.avapir
     * .roguelike.core.Game#mobs}
     * @return null or removed mob
     */
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
                    panel.drawToCell(g2, panel.getImage(itemsHere.size() > 1 ? "many_items" : itemsHere.get(0)
                                                                                                       .getItem()
                                                                                                       .getData()
                                                                                                       .getImageName
                                                                                                               ()), j, i
                                    );
                }
            } else {
                if (isSeen()) {
                    panel.drawToCell(g2, panel.getImage("wFog"), j, i);
                }
            }
        }
    }
}
