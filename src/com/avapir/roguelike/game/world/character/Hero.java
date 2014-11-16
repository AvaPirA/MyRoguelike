package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.GameStateManager;
import com.avapir.roguelike.core.GameStateManager.GameState;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.avapir.roguelike.game.world.items.DroppedItem;
import com.avapir.roguelike.game.world.items.Item;
import com.avapir.roguelike.game.world.map.MapHolder;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This class implements special type of mob with stats, inventory and equipment. Accordingly,
 * this means that this mob has greater secondary stats, may drop some items and has greater secondary stats again.
 * Also it handles few cases for main hero (e.g. game must end if main hero is dead).
 */
public class Hero extends Mob {

    /** Amounts of XP needed to level up */
    private static final int[] XP_TO_LVL = {0, 68, 295, 805, 1716, 3154, 5249, 8136, 11955, 16851, 22978, 30475,
            39516, 50261, 62876, 77537, 94421, 113712, 135596, 160266, 84495, 95074, 107905, 123472, 142427, 165669,
            194509, 231086, 279822, 374430, 209536, 248781, 296428, 354546, 425860, 514086, 624568, 765820, 954872};
    private static final Hero  instance  = new Hero("Hero");
    /** {@link InventoryHandler} instance. Responsible for hero's items storage */
    private final InventoryHandler inventory;
    /** {@link EquipmentHandler} instance. Responsible for items that hero equipped */
    private final EquipmentHandler equipment;
    /** {@link PrimaryStats} instance. Responsible for stats of hero that may be found in the calculation formulas */
    private final PrimaryStats     stats;
    /** Current hero's level */
    private       int              level;
    /** Current amount of experience */
    private       int              XP;

    /**
     * Creates new hero but don't put him somewhere on the map
     *
     * @param name displayable name
     */
    private Hero(String name) {
        /* Name, maxHp, maxMp, attack, armor, location, map, ai*/
//        super(name, 1, 1, null, null, UNRESOLVED_LOCATION, null, IdleAI.getNewInstance()); // same as next line
        super(name);
        stats = new PrimaryStats(name);
        level = 1;
        XP = 0;
        inventory = new InventoryHandler();
        equipment = new EquipmentHandler(inventory);
        restore();
    }

    /**
     * That class describes default stats which are applied to every new instance of corresponding Hero
     */
    private static final class DefaultStats {
        /* 	STR 	AGI 	VIT 	INT 	DEX 	LUK */
        private static final int[] PLAYER = {3, 3, 3, 3, 2, 1};    // 16
        private static final int[] NPC    = {50, 100, 100, 50, 50, 10};  // 360
        private static final int[] ELDER  = {290, 120, 390, 700, 400, 100};    // 2000
        private static final int[] UNDEAD = {120, 40, 120, 0, 40, 0};    // 320

        //private static final int[] PLAYER = {280, 170, 230, 90, 70, 47}; // 887 test values
    }

    /**
     * Contains all formulas used for every secondary parameters: attack or defense power, hp and mp values,
     * FoV radius, endurance and so on. Also here is some sugar for primary hero stats, but that methods .
     */
    public static final class StatsFormulas {

        public static float getMaxHP(final Hero h) {
            final int baseHP = 2;
            final int STR = getStr(h);
            final int VIT = getVit(h);
            return baseHP + 4 * STR + 7 * VIT;
        }

        public static float getMaxMP(final Hero h) {
            final int baseMP = 1;
            final int INT = getInt(h);
            return baseMP + 7 * INT;
        }

        public static double addBonusXp(final Hero h, final double xp) {
            final Random r = new Random();
            final double L = r.nextInt(h.stats.getLuk() * 2);
            // up to 100% bonus for each 50 LUK
            return (int) (xp * (1f + h.stats.getDex() / 300f + L / 100f));
        }

        public static int getFovRadius(final Hero h) {
            final int baseFOVR = 5;
            final int INT = getInt(h);
            return baseFOVR + INT / 50;
        }

        public static int getAtkRadius(final Hero h) {
            final int AGI = getAgi(h);
            return AGI == 0 ? 0 : AGI < 50 ? 1 : AGI < 150 ? 2 : 3;
        }

        public static Attack getAttack(final Hero h) {
            final float STR = getStr(h);
            final float DEX = getDex(h);
            final float INT = getInt(h);
            final float phy = 1.6f + STR + DEX * 0.4f + INT * 0.2f;
            final float mag = 1.2f + INT + DEX * 0.4f;
            return new Attack(phy, mag);
        }


        public static Armor getArmor(final Hero h) {
            final float STR = getStr(h);
            final float AGI = getAgi(h);
            final float INT = getInt(h);
            final float phy = AGI * 0.7f + STR * 0.3f;
            final float mag = INT * 0.5f;
            return new Armor(phy, mag);
        }

        public static boolean isOverweighted(Hero hero) {
            return hero.equipment.getWeight() > getMaxWeight(hero);
        }

        public static int getMaxWeight(Hero hero) {
            //fixme fix that out of the air formula
            return 30 * getStr(hero);
        }

        private static int getStr(final Hero h) {
            return getStat(h, 0);
        }

        private static int getAgi(final Hero h) {
            return getStat(h, 1);
        }

        private static int getVit(final Hero h) {
            return getStat(h, 2);
        }

        private static int getInt(final Hero h) {
            return getStat(h, 3);
        }

        private static int getDex(final Hero h) {
            return getStat(h, 4);
        }

        private static int getLuk(final Hero h) {
            return getStat(h, 5);
        }

        private static int getStat(final Hero h, final int i) {
            int STAT = h.getStats().values(i);
            final GameState s = GameStateManager.getInstance().getState();
            if (s == GameState.CHANGE_STATS) {
                STAT += GameStateManager.getInstance().getStatsHandler().getDiff()[i];
            }
            return STAT;
        }

        //todo new formulas

    }

    /**
     * Here is the main hero's stats. They are used to compute formulas from {@link StatsFormulas}. Each time hero
     * reaches next level, he gains few "free stats" which can be spend on purchase any primary stat.
     */
    public static final class PrimaryStats {

        /** STRength <br> AGIlity <br> VITality <br> INTelligence <br> DEXterity <br> LUcK */
        public static final  String[] STATS_STRINGS         = {"STR", "AGI", "VIT", "INT", "DEX", "LUK"};
        /** Total amount of stats */
        public static final  int      PRIMARY_STATS_AMOUNT  = STATS_STRINGS.length;
        /** Maximum value which can have any stat */
        public static final  int      MAX_STAT_VALUE        = 300;
        /** Amount of "free stats" gained on each level up */
        private static final int      DEFAULT_STAT_INCREASE = 5;
        /** Stats storage */
        private final        int[]    values                = new int[PRIMARY_STATS_AMOUNT];
        /** Amount of available "free stats". Default value is amount of "free stats" available for new Hero */
        private              int      freeStats             = 100000;

        /**
         * Creates new stats instance for corresponding hero type
         *
         * @param name displayable hero name
         */
        public PrimaryStats(final String name) {
            // TODO name prefix recognition == crap
            int[] defaultStats;
            if (name.contains("NPC")) {
                defaultStats = DefaultStats.NPC;
            } else if (name.contains("Elder")) {
                defaultStats = DefaultStats.ELDER;
            } else if (name.contains("Undead")) {
                defaultStats = DefaultStats.UNDEAD;
            } else {
                defaultStats = DefaultStats.PLAYER;
            }
            System.arraycopy(defaultStats, 0, values, 0, PRIMARY_STATS_AMOUNT);
        }

        /**
         * Allows to iterate through stats array. Used in painting GUI.
         *
         * @param i index of requested stat
         *
         * @return requested stat value
         *
         * @throws java.lang.ArrayIndexOutOfBoundsException if {@code i < 0} or {@code i > }
         *                                                  {@link #PRIMARY_STATS_AMOUNT}
         */
        public int values(final int i) {return values[i];}

        public int getStr() {return values[0];}

        public int getAgi() {return values[1];}

        public int getVit() {return values[2];}

        public int getInt() {return values[3];}

        public int getDex() {return values[4];}

        public int getLuk() {return values[5];}

        /**
         * @param i stat index
         *
         * @return {@code true} if that stat can not be increased further
         */
        public boolean isMaxed(final int i) {
            return values[i] >= MAX_STAT_VALUE;
        }

        /**
         * Changes specified stat by specified value
         *
         * @param cursor index of stat
         * @param value  will be added to specified stat. May be negative
         */
        public void changeStatBy(final int cursor, final int value) {
            values[cursor] += value;
        }

        /**
         * @return {@code true} if Hero has at least one "free stat"
         */
        public boolean isLearnable() {
            return freeStats > 0;
        }

        public int getFreeStats() {
            return freeStats;
        }

        /**
         * Changes amount of "free stats" by specified value
         *
         * @param freeDiff will be added to {@link #freeStats}. May be negative
         */
        public void changeFreeBy(final int freeDiff) {
            freeStats += freeDiff;
        }
    }

    /**
     * That is representation of hero's backpack.<br/>
     * You can put here items, you can get items from herem you can move items inside the inventory,
     * you can stack similar items into one cell, you can split one cell into two,
     * you can enlarge inventory (maybe with some special items or NPC).
     */
    public static final class InventoryHandler {
        //todo inventory sorting
        //todo InventoryHandler is too binded with it's View. Need to escape this and to do it in right MVC way

        public static final int    LINE          = 8;
        private             int    inventorySize = 3;
        private             Item[] storage       = new Item[LINE * (inventorySize + 3)];
        /** Amount of cells which stores some items */
        private int occupied;

        public InventoryHandler() {
//            put(new Item(1));
//            put(new Item(2));
//            put(new Item(3));
//            put(new Item(4, 20));
        }

        public static int coordToCell(int x, int y) {
            return y * LINE + x;
        }

        public static int coordToCell(Point p) {
            return coordToCell(p.x, p.y);
        }

        /**
         * Adds new line ({@value #LINE} cells) to storage
         */
        public void enlarge() {
            Item[] tmp = new Item[LINE * (inventorySize)];
            System.arraycopy(storage, 0, tmp, 0, tmp.length); //save state
            storage = new Item[LINE * (++inventorySize)]; //enlarge
            System.arraycopy(tmp, 0, storage, 0, tmp.length); //restore
        }

        /**
         * Puts new item to inventory. If there is no items of that type, the new ones will be placed in first occurred
         * empty cell. Either they will be stacked.
         *
         * @param item acquired item(s)
         *
         * @throws IllegalArgumentException if there is no applicable cell
         */
        public synchronized void put(Item item) {
            if (free() == 0) {throw new IllegalArgumentException("No empty space");}
            int index = find(item);
            if (index < 0) {
                int firstEmpty = find(null);
                if (firstEmpty < 0) { throw new IllegalArgumentException("No empty space"); }
                storage[firstEmpty] = item;
                occupied++;
            } else {
                storage[index].increase(item.getAmount());
            }
        }

        /**
         * @param index index of cell for item to return
         *
         * @return item from specified cell
         */
        private Item get(int index) {
            return storage[index];
        }

        public Item get(Point p) {
            return get(coordToCell(p));
        }

        /**
         * @param item type of items
         *
         * @return amount of items of specified type
         */
        public synchronized int getAmount(Item item) {
            if (item == null) {
                return 0;
            }
            int sum = 0;
            for (Item i : storage) {
                if (item.equals(i)) {
                    sum += i.getAmount();
                }
            }
            return sum;
        }

        /**
         * @param index index of cell to explore
         *
         * @return amount of items into the specified cell
         */
        public synchronized int getAmount(int index) {
            checkIndex(index);
            return storage[index] == null ? 0 : storage[index].getAmount();
        }

        /**
         * @param index any number
         *
         * @throws IllegalArgumentException if specified number is not a suitable index for storage array. Actually it
         *                                  means <br>{@code index < 0 || index >= capacity()}</br>
         */
        private void checkIndex(int index) {
            if (index < 0 || index >= capacity()) {
                throw new IllegalArgumentException("Wrong index");
            }
        }

        /**
         * @param item type of item
         *
         * @return index of first occurrence of item of specified type
         */
        public synchronized int find(Item item) {
            return find(item, 0);
        }

        /**
         * @param item type of item
         * @param skip how much occurrences must be skipped
         *
         * @return index of {@code skip+1} occurrence of item of specified type
         */
        private int find(Item item, int skip) {
            if (item == null) {
                if (free() < 1) {
                    return -1; //no empty space
                }
                for (int i = 0; i < capacity(); i++) {
                    if (storage[i] == null && skip-- == 0) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < capacity(); i++) {
                    if (item.equals(storage[i]) && skip-- == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Removes all items of specified type from storage
         *
         * @param item type of item
         *
         * @return type and total amount of removed items
         */
        public synchronized Item remove(Item item) {
            if (item == null) {return null;}
            Item tmp = new Item(item);
            for (int i : findAll(item)) {
                tmp.increase(remove(i).getAmount());
            }
            return tmp;
        }

        /**
         * Clears cell specified by index
         *
         * @param index index of cell
         *
         * @return removed items
         */
        public synchronized Item remove(int index) {
            checkIndex(index);
            Item tmp = storage[index];
            if (tmp == null) { return null; }
            storage[index] = null;
            occupied--;
            return tmp;
        }

        /**
         * Removes {@code amount} items of {@code item} type from storage
         *
         * @param item   type of item
         * @param amount how much items must be removed
         *
         * @return type and total amount of removed items
         *
         * @throws IllegalArgumentException if amount of stored items less than {@code amount}
         */
        public synchronized Item remove(Item item, int amount) {
            if (item == null) { return null; }
            int nominalAmount = getAmount(item);
            if (nominalAmount == amount) { return remove(item); }
            if (nominalAmount < amount) { throw new IllegalArgumentException("Not enough items");}

            Iterator<Integer> iter = findAll(item).iterator();
            while (amount > 0) {
                int index = iter.next();
                int localNominalAmount = getAmount(index);
                if (localNominalAmount > amount) {
                    remove(index, amount);
                    break;
                } else {
                    remove(index);
                    amount -= localNominalAmount;
                }
            }
            return new Item(item, amount);
        }

        /**
         * @param item type of items
         *
         * @return all occurrences of cells with items of specified type
         */
        public List<Integer> findAll(Item item) {
            List<Integer> indexes = new ArrayList<>();
            if (item == null) {
                for (int i = 0; i < capacity(); i++) {
                    if (storage[i] == null) {
                        indexes.add(i);
                    }
                }
            } else {
                for (int i = 0; i < capacity(); i++) {
                    if (item.equals(storage[i])) {
                        indexes.add(i);
                    }
                }
            }
            return indexes;
        }

        /**
         * Decreases amount of items in specified cell
         *
         * @param index  index of cell
         * @param amount amount of items to remove
         *
         * @return type and total amount of removed items
         */
        public synchronized Item remove(int index, int amount) {
            checkIndex(index);
            if (storage[index] == null) { return null; }
            if (storage[index].getAmount() == amount) {
                return remove(index);
            } else {
                Item divided = storage[index];
                divided.decrease(amount);
                return new Item(divided, amount);
            }
        }

        /**
         * @param item type of items
         *
         * @return {@code true} if at least one cell stores items of specified type
         */
        public synchronized boolean contains(Item item) {
            if (item == null) {
                return free() > 0;
            } else {
                for (Item i : storage) {
                    if (item.equals(i)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Unites items of similar type from cells {@code from} and {@code to} into cell {@code to}
         *
         * @param from cell, where to get
         * @param to   cell, where to put the union
         */
        private synchronized void unite(int from, int to) {
            if (storage[to] == null) {
                if (storage[from] != null) {
                    storage[to] = storage[from];
                    storage[from] = null; //decrease(getAmount)
                    occupied--;
                } // else both are null
            } else {
                if (storage[from] != null) {
                    if (storage[from].equals(storage[to])) {
                        storage[to].increase(storage[from].getAmount());
                        storage[from] = null; //decrease(getAmount)
                        occupied--;
                    }
                }
            }
        }

        public synchronized void move(Point p1, Point p2) {
            move(coordToCell(p1), coordToCell(p2));
        }

        /**
         * Swaps two cells.
         *
         * @param from first swapping cell
         * @param to   second swapping cell
         */
        public synchronized void move(int from, int to) {
            if (storage[to] != null) {
                if (storage[from] != null) {
                    // non-null
                    if (storage[from].equals(storage[to])) {
                        unite(from, to);
                    } else {
                        storage[from].swap(storage[to]);
                    }
                } else {
                    // from -> null; to -> Item
                    storage[from] = storage[to];
                    storage[to] = null;
                }
            } else {
                // to -> null;
                if (storage[from] != null) {
                    // from -> Item
                    storage[to] = storage[from];
                    storage[from] = null;
                }
            }
        }

        /**
         * Move some items from cell {@code from} to cell {@code to}.
         *
         * @param from   cell, where to get
         * @param to     cell, where to put gathered items
         * @param amount how much items to transfer
         */
        public synchronized void split(int from, int to, int amount) {
            if (amount < 1) { throw new IllegalArgumentException("You must move more than one item"); }
            if (storage[from] != null) {
                if (storage[to] == null) {
                    Item divided = storage[from];
                    divided.decrease(amount);
                    storage[to] = new Item(divided, amount);
                } else if (storage[from].equals(storage[to])) {
                    storage[from].decrease(amount);
                    storage[to].increase(amount);
                    occupied++;
                }
            }
        }

        /**
         * @return maximum possible number of stored items
         */
        public int capacity() {
            return storage.length;
        }

        /**
         * @return amount of free cells in storage
         */
        public int free() {
            return capacity() - occupied;
        }

        /**
         * Returns amount of lines in inventory available for storing smth.
         * <p>
         * By default it's 3. Further in game player will be able to get additional lines by some vendor NPC or by some
         * quest.
         *
         * @return amount of lines already achieved.
         */
        public int getSize() {
            return inventorySize;
        }

        /**
         * Collects significant information about inventory when painting module requested it.
         *
         * @return array which stores information about all stored items (and only about items: we don't care about
         * empty cells): coords, amount and id (to map image)
         */
        public int[][] toPaintableArrays() {
            int[][] arrays = new int[occupied][4];
            int cursor = 0;
            for (int i = 0; i < storage.length; i++) {
                if (storage[i] != null) {
                    arrays[cursor][0] = i / LINE; //x
                    arrays[cursor][1] = i % LINE; //y
                    arrays[cursor][2] = storage[i].getID(); //image
                    arrays[cursor][3] = storage[i].getAmount(); //nuff said
                    cursor++;
                }
            }
            return arrays;
        }
    }

    /**
     * This is representation of hero's clothing, that contains: helm, vest, gloves, leggins, boots, necklace,
     * 2 weapons (or one two-handed), 2 rings, 2 artifacts. Also this class can compute all secondary stats which
     * equipped items provide.
     */
    public static final class EquipmentHandler {
        //todo maybe this class must be moved into InventoryHandler as inner(non-static nested) class?

        /** Total amount of places where some clothing may be put */
        private static final int SLOTS = 3 * 4;

        /**
         * art1  helm  art2
         * neck  vest  wep2
         * wep1  legg  glov
         * rng1  boot  rng2
         */
        private final Item[] equip = new Item[SLOTS];

        /** Inventory instance from where hero gets items to wear */
        private final InventoryHandler inventory;
        /** Cached attack value */
        private       Attack           attack;
        /** Cached armor value */
        private       Armor            armor;
        /** Cached weight value */
        private       int              weight;

        public EquipmentHandler(InventoryHandler inventory) {
            this.inventory = inventory;
        }

        /**
         * @param index slot index
         *
         * @return item dressed in specified slot
         */
        public Item get(int index) {
            if (index < 0 || index >= SLOTS) {throw new IllegalArgumentException("Wrong slot number");}
            return equip[index];
        }

        public Attack getAttack() {
            return attack;
        }

        public Armor getArmor() {
            return armor;
        }

        public int getWeight() {
            return weight;
        }

        /**
         * Update cache values when some item was put on or taken off
         */
        private void onChangeEquipment() {
            attack = new Attack();
            armor = new Armor();
            weight = 0;
            for (Item i : equip) {
                if (i != null) {
                    attack.addAttack(i.getData().getAttack());
                    armor.addArmor(i.getData().getArmor());
                    weight += i.getData().getWeight();
                }
            }
        }

        /**
         * Puts one item from inventory to specified slot into equipment
         *
         * @param p    from where to get item
         * @param slot where to put item
         */
        public void putOn(Point p, ClothingSlots slot) {
            putOn(InventoryHandler.coordToCell(p), slot);
        }

        /**
         * Puts one item from inventory to specified slot into equipment
         *
         * @param index from where to get item
         * @param slot  where to put item
         */
        private void putOn(int index, ClothingSlots slot) {
            Item stored = inventory.get(index);
            if (stored == null) {return;}
            int i = slot.ordinal();
            if (equip[i] != null) {
                inventory.remove(index).swap(equip[i]);
            } else {
                equip[i] = inventory.remove(index);
            }

            onChangeEquipment();
        }

        public Item get(ClothingSlots slot) {
            return equip[slot.ordinal()];
        }

        /**
         * @param slots specified slot
         *
         * @return amount of items dressed in specified slot (usable for e.g. arrows)
         */
        public int getAmount(ClothingSlots slots) {
            return equip[slots.ordinal()].getAmount();
        }

        /**
         * Takes off item from specified slot of equipment to inventory. If inventory if full nothing will happen.
         *
         * @param slot specified slot
         *
         * @return item that was taken off
         */
        public Item takeOff(ClothingSlots slot) {
            if (inventory.free() > 0) {
                int i = slot.ordinal();
                Item tmp = equip[i];
                equip[i] = null;
                inventory.put(tmp);
                onChangeEquipment();
                return tmp;
            } else {
                Log.g("Can't take off due full inventory");
                return null;
            }
        }

    }

    public static Hero getInstance() { return instance;}

    public EquipmentHandler getEquipment() {
        return equipment;
    }

    /**
     * Restores main character's parameters to default (computed with {@link StatsFormulas})
     */
    private void restore() {
        maxHP = StatsFormulas.getMaxHP(this);
        maxMP = StatsFormulas.getMaxMP(this);
        HP = maxHP;
        MP = maxMP;
        attack.replaceBy(StatsFormulas.getAttack(this));
        armor.replaceBy(StatsFormulas.getArmor(this));
    }

    /**
     * Updates main character's parameter while player changes stats (while he distributes "free stats"). Hp/mp
     * percentage will remain to prevent cheat-healing.
     */
    public void updateStats() {
        final float hpPercentage = HP / maxHP;
        final float mpPercentage = MP / maxMP;

        maxHP = StatsFormulas.getMaxHP(this);
        maxMP = StatsFormulas.getMaxMP(this);
        HP = maxHP * hpPercentage;
        MP = maxMP * mpPercentage;
        attack.replaceBy(StatsFormulas.getAttack(this));
        armor.replaceBy(StatsFormulas.getArmor(this));
    }

    public PrimaryStats getStats() {
        return stats;
    }

    /**
     * Computes amount of XP gained from dealt damage and adds bonus XP. After that method will check if new level
     * requirements was met.
     *
     * @param dmg dealt damage
     */
    public void gainXpFromDamage(final float dmg) {
        //todo move formula to StatsFormulas
        final int xp = (int) Math.pow(dmg, 6 / 5f);
        final int gainedXP = (int) StatsFormulas.addBonusXp(this, xp);
        XP += gainedXP;
        Log.g("%s получает %s опыта", getName(), gainedXP);

        //todo extract method
        while (XP >= XP_TO_LVL[level]) {
            XP = 0;
            level++;
            stats.freeStats += PrimaryStats.DEFAULT_STAT_INCREASE;
            restore();
            Log.g("%s достиг %s уровня!", getName(), level);
        }
    }

    /**
     * {@inheritDoc}
     * Unable to move while carrying to heavy equipment
     */
    @Override
    public Point move(final Point dp) {
        if (StatsFormulas.isOverweighted(this)) {
            Log.g("Вы #2#перегружены!#^#");
            return new Point(0, 0);
        }
        return super.move(dp);
    }

    /**
     * Puts items from tile on which hero stands to inventory (while it has free space)
     */
    public void pickUpItems() {
        List<DroppedItem> items = MapHolder.getInstance().getTile(getLoc().x, getLoc().y).getItemList();
        Iterator<DroppedItem> iter = items.iterator();
        try {
            while (iter.hasNext()) {
                inventory.put(iter.next().getItem());
                iter.remove();
            }
        } catch (IllegalArgumentException e) {
            //no free space
        }
    }

    /**
     * {@inheritDoc}
     * Death of main hero will end the game.
     */
    @Override
    protected void onDeath() {
        GameStateManager g = GameStateManager.getInstance();
        g.gameOver();
        g.repaint();
    }

    @Override
    public Armor getArmor() {
        return Armor.sum(super.getArmor(), equipment.getArmor());
    }

    @Override
    public float getArmor(final int i) {
        return getArmor().getArmor(i);
    }

    @Override
    public Attack getAttack() {
        return Attack.sum(super.getAttack(), equipment.getAttack());
    }

    @Override
    public float getAttack(final int i) {
        return getAttack().getDamageOfType(i);
    }

    public int getXP() {
        return XP;
    }

    /**
     * @return amount of XP needed to reach next level
     */
    public int getAdvanceXP() {
        return XP_TO_LVL[level];
    }

    public int getLevel() {
        return level;
    }

    public InventoryHandler getInventory() {
        return inventory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void moveTo(Point newLoc) {
        super.moveTo(newLoc);

        Tile t = MapHolder.getInstance().getTile(newLoc.x, newLoc.y);
        switch (t.getItemsAmount()) {
            case 1:
                Log.g("Здесь есть %s.", t.getItemList().get(0).getItem().getData().getName());
            case 0:
                break;
            default:
                Log.g("Здесь лежит много вещей.");
                break;
        }
    }

    /**
     * Attacks hostile mob and gains experience from dealt damage
     *
     * @param newLoc there to go
     *
     * @return dealt damage
     */
    @Override
    protected float moveAttack(Point newLoc) {
        float damage = super.moveAttack(newLoc);
        gainXpFromDamage(damage);
        return damage;
    }
}
