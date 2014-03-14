package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.ClothingSlots;
import com.avapir.roguelike.game.ai.IdleAI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Hero extends Mob implements Locatable {
    public EquipmentHandler getEquipment() {
        return equipment;
    }

    /** Stolen from L2 */
    private static final int[] XP_TO_LVL = {0, 68, 295, 805, 1716, 3154, 5249, 8136, 11955, 16851, 22978, 30475,
            39516, 50261, 62876, 77537, 94421, 113712, 135596, 160266, 84495, 95074, 107905, 123472, 142427, 165669,
            194509, 231086, 279822, 374430, 209536, 248781, 296428, 354546, 425860, 514086, 624568, 765820, 954872};
    private final InventoryHandler inventory;
    private final EquipmentHandler equipment;
    private final PrimaryStats     stats;
    private final Game             game;
    private       int              level;
    private       int              XP;

    public Hero(String name, Game g) {
        super(name, 1, 1, null, null, UNRESOLVED_LOCATION, IdleAI.getNewInstance());
        stats = new PrimaryStats(name);
        game = g;
        level = 1;
        XP = 0;
        inventory = new InventoryHandler();
        equipment = new EquipmentHandler(this);
        restore();
    }

    private static final class DefaultStats {
        /* 	STR 	AGI 	VIT 	INT 	DEX 	LUK */
        static final int[] PLAYER = {3, 3, 3, 3, 2, 1};    // 16
        //		static final int[]	PLAYER	= { 280, 	170,	230,	90,		70,		47 };	// 887
        static final int[] NPC    = {50, 100, 100, 50, 50, 10};    // 360
        static final int[] ELDER  = {290, 120, 390, 700, 400, 100};    // 2000
        static final int[] UNDEAD = {120, 40, 120, 0, 40, 0};    // 320
    }

    public static final class StatsFormulas {

        public static float getMaxHP(final Hero h) {
            final int baseHP = 2;
            final int STR = getStr(h);
            final int VIT = getVit(h);
            return baseHP + 4 * STR + 7 * VIT;
        }

        private static int getVit(final Hero h) {
            return getStat(h, 2);
        }

        private static int getStr(final Hero h) {
            return getStat(h, 0);
        }

        private static int getStat(final Hero h, final int i) {
            int STAT = h.getStats().values(i);
            final GameState s = h.game.getState();
            if (s == GameState.CHANGE_STATS) {
                STAT += h.game.getStatsHandler().getDiff()[i];
            }
            return STAT;
        }

        public static float getMaxMP(final Hero h) {
            final int baseMP = 1;
            final int INT = getInt(h);
            return baseMP + 7 * INT;
        }

        private static int getInt(final Hero h) {
            return getStat(h, 3);
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

        private static int getAgi(final Hero h) {
            return getStat(h, 1);
        }

        public static Attack getAttack(final Hero h) {
            final float STR = getStr(h);
            final float DEX = getDex(h);
            final float INT = getInt(h);
            final float phy = 1.6f + STR + DEX * 0.4f + INT * 0.2f;
            final float mag = 1.2f + INT + DEX * 0.4f;
            return new Attack(phy, mag);
        }

        private static int getDex(final Hero h) {
            return getStat(h, 4);
        }

        public static Armor getArmor(final Hero h) {
            final float STR = getStr(h);
            final float AGI = getAgi(h);
            final float INT = getInt(h);
            final float phy = AGI * 0.7f + STR * 0.3f;
            final float mag = INT * 0.5f;
            return new Armor(phy, mag);
        }

        private static int getLuk(final Hero h) {
            return getStat(h, 5);
        }

        public static boolean isOverweighted(Hero hero) {
            return hero.equipment.getWeight() > getMaxWeight(hero);
        }

        public static int getMaxWeight(Hero hero) {
            //todo
            return 30 * getStr(hero);
        }

    }

    public static final class PrimaryStats {

        public static final String[] STATS_STRINGS         = {"STR", "AGI", "VIT", "INT", "DEX", "LUK"};
        /** STRength <br> AGIlity <br> VITality <br> INTelligence <br> DEXterity <br> LUcK */
        public static final int      PRIMARY_STATS_AMOUNT  = STATS_STRINGS.length;
        public static final int      DEFAULT_STAT_INCREASE = 5;
        public static final int      MAX_STAT_VALUE        = 300;
        private final       int[]    values                = new int[PRIMARY_STATS_AMOUNT];
        private             int      freeStats             = 100000;

        //@formatter:off
        public PrimaryStats(final String name) {
            if (name.contains("NPC")) {
                ac(DefaultStats.NPC);
            } else if (name.contains("Elder")) {
                ac(DefaultStats.ELDER);
            } else if (name.contains("Undead")) {
                ac(DefaultStats.UNDEAD);
            } else {
                ac(DefaultStats.PLAYER);
            }
        }

        private void ac(final int[] a) {
            System.arraycopy(a, 0, values, 0, PRIMARY_STATS_AMOUNT);
        }

        public int values(final int i) {return values[i];}

        public int[] getArray() {return values;}

        public int getStr() {return values[0];}

        public int getAgi() {return values[1];}

        public int getVit() {return values[2];}

        public int getInt() {return values[3];}

        public int getDex() {return values[4];}

        public int getLuk() {return values[5];}
        //@formatter:on

        public boolean isMaxed(final int i) {
            return values[i] >= MAX_STAT_VALUE;
        }

        public void decrease(final int cursor) {
            decreaseBy(cursor, 1);
            freeStats++;
        }

        public void decreaseBy(final int cursor, final int value) {
            values[cursor] -= value;
        }

        public void increase(final int cursor) {
            if (freeStats > 0) {
                increaseBy(cursor, 1);
                freeStats--;
            }
        }

        public void increaseBy(final int cursor, final int value) {
            values[cursor] += value;
        }

        public boolean hasFreeStats() {
            return freeStats > 0;
        }

        public int getFree() {
            return freeStats;
        }

        public void changeFreeBy(final int freeDiff) {
            freeStats += freeDiff;
        }
    }

    public static final class InventoryHandler {

        private             int    inventorySize = 3;
        public static final int    LINE          = 8;
        private             Item[] storage       = new Item[LINE * (inventorySize + 3)];
        private int occupied;

        /**
         * Adds new line (==8 cells) to storage
         */
        public void enlarge() {
            Item[] tmp = new Item[LINE * (inventorySize)];
            System.arraycopy(storage, 0, tmp, 0, tmp.length); //save state
            storage = new Item[LINE * (++inventorySize)]; //enlarge
            System.arraycopy(tmp, 0, storage, 0, tmp.length); //restore
        }

        /**
         * Puts new item to inventory. If there is no items of that, the new ones will be placed in first occurred
         * empty
         * cell. Either they will be stacked.
         *
         * @param item acquired item(s)
         *
         * @throws IllegalArgumentException if there is no applicable cell
         */
        public synchronized void put(Item item) {
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
        public Item get(int index) {
            return storage[index];
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
         *                                  means <br>{@code index < 0 || index >= size()}</br>
         */
        private void checkIndex(int index) {
            if (index < 0 || index >= size()) {
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
                for (int i = 0; i < size(); i++) {
                    if (storage[i] == null && skip-- == 0) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < size(); i++) {
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
                for (int i = 0; i < size(); i++) {
                    if (storage[i] == null) {
                        indexes.add(i);
                    }
                }
            } else {
                for (int i = 0; i < size(); i++) {
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
        public synchronized void unite(int from, int to) {
            if (storage[to] == null) {
                if (storage[from] != null) {
                    storage[to] = storage[from];
                } // else both are null
            } else {
                if (storage[from] != null) {
                    if (storage[from].equals(storage[to])) {
                        storage[to].increase(storage[from].getAmount());
                        storage[from] = null; //decrease(getAmount)
                    }
                }
            }
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
                    storage[from].swap(storage[to]);
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
                }
            }
        }

        /**
         * @return maximum possible number of stored items
         */
        public int size() {
            return storage.length;
        }

        /**
         * @return amount of free cells in storage
         */
        public int free() {
            return size() - occupied;
        }

        /**
         * Returns amount of lines in inventory available for storing smth.
         * <p/>
         * By default it's 3. Further in game player will be able to get additional lines by some vendor NPC or by some
         * quest.
         *
         * @return amount of lines already achieved.
         */
        public int getSize() {
            return inventorySize;
        }

        public int[][] toPaintableArrays() {
            int[][] arrays = new int[occupied][4];
            int cursor = 0;
            for (int i = 0; i < storage.length; i++) {
                if (storage[i] != null) {
                    arrays[cursor][0] = i / LINE; //x
                    arrays[cursor][1] = i % LINE; //y
                    arrays[cursor][2] = storage[i].getID(); //image
                    arrays[cursor][3] = storage[i].getAmount(); //nuff said
                }
            }
            return arrays;
        }
    }

    public static final class EquipmentHandler {

        private static final int SLOTS = 3 * 4;

        /**
         * art1  helm  art2
         * neck  vest  wep2
         * wep1  legg  glov
         * rng1  boot  rng2
         */
        private Item[] equip = new Item[SLOTS];
        private Hero   hero;
        private Attack attack;
        private Armor  armor;
        private int    weight;

        public EquipmentHandler(Hero hero) {
            this.hero = hero;
        }

        public Item getDressed(int index) {
            if (index < 0 || index >= SLOTS) {throw new IllegalArgumentException("Wrong slot number");}
            return equip[index];
        }

        public Item getDressed(ClothingSlots slot) {
            return equip[slot.ordinal()];
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

        private void onChangeEquipment() {
            attack = new Attack();
            armor = new Armor();
            weight = 0;
            for (Item i : equip) {
                attack.addAttack(i.getData().getAttack());
                armor.addArmor(i.getData().getArmor());
                weight += i.getData().getWeight();
            }
        }

        public void putOn(int index, ClothingSlots slot) {
            Item stored = hero.inventory.get(index);
            if (stored == null) {return;}
            int i = slot.ordinal();
            if (equip[i] != null) {
                equip[i].swap(stored);
            }
            onChangeEquipment();
        }

        public Item get(ClothingSlots slot) {
            return equip[slot.ordinal()];
        }

        public int getAmount(ClothingSlots slots) {
            return equip[slots.ordinal()].getAmount();
        }

        public Item takeOff(ClothingSlots slot) {
            if (hero.inventory.free() > 0) {
                int i = slot.ordinal();
                Item tmp = equip[i];
                equip[i] = null;
                hero.inventory.put(tmp);
                onChangeEquipment();
                return tmp;
            } else {
                throw new IllegalStateException("No empty space in inventory");
            }
        }

    }

    private void restore() {
        maxHP = Hero.StatsFormulas.getMaxHP(this);
        maxMP = Hero.StatsFormulas.getMaxMP(this);
        HP = maxHP;
        MP = maxMP;
        attack.replaceBy(Hero.StatsFormulas.getAttack(this));
        armor.replaceBy(Hero.StatsFormulas.getArmor(this));
    }

    public void updateStats() {
        final float hpPercentage = HP / maxHP;
        final float mpPercentage = MP / maxMP;

        maxHP = Hero.StatsFormulas.getMaxHP(this);
        maxMP = Hero.StatsFormulas.getMaxMP(this);
        HP = maxHP * hpPercentage;
        MP = maxMP * mpPercentage;
        attack.replaceBy(Hero.StatsFormulas.getAttack(this));
        armor.replaceBy(Hero.StatsFormulas.getArmor(this));
    }

    public PrimaryStats getStats() {
        return stats;
    }

    public void gainXpFromDamage(final float dmg) {
        final int xp = (int) Math.pow(dmg, 6 / 5f);
        final int gainedXP = (int) StatsFormulas.addBonusXp(this, xp);
        XP += gainedXP;
        Log.g("%s получает %s опыта", getName(), gainedXP);
        while (lvlUp()) {
            gainLvl();
        }

    }

    private void gainLvl() {
        XP = 0;
        level++;
        stats.freeStats += PrimaryStats.DEFAULT_STAT_INCREASE;
        restore();
        Log.g("%s достиг %s уровня!", getName(), level);
    }

    private boolean lvlUp() {
        return XP >= XP_TO_LVL[level];
    }

    @Override
    public Point move(final Point dp, final Game g) {
        if (StatsFormulas.isOverweighted(this)) {
            Log.g("Вы #2#перегружены!#^#");
            return new Point(0, 0);
        }
        return super.move(dp, g);
    }

    @Override
    protected void onDeath(final Game g) {
        g.gameOver();
        g.repaint();
    }

    @Override
    public Armor getArmor() {
        return super.getArmor().addArmor(equipment.getArmor());
    }

    @Override
    public float getArmor(final int i) {
        return getArmor().getArmor(i);
    }

    @Override
    public Attack getAttack() {
        return super.getAttack().addAttack(equipment.getAttack());
    }

    @Override
    public float getAttack(final int i) {
        return getAttack().getDamageOfType(i);
    }

    public int getXP() {
        return XP;
    }

    public int getAdvanceXP() {
        return XP_TO_LVL[level];
    }

    public int getLevel() {
        return level;
    }

    public InventoryHandler getInventory() {
        return inventory;
    }

    @Override
    public void paint(AbstractGamePanel panel, Graphics2D g2, int j, int i) {
        if (isAlive()) {
            if (this == game.getHero()) {
                panel.drawToCell(g2, panel.getImage("hero"), j, i);
                paintColorBar(getHP() / getMaxHp(), new Color(0, 255, 0, 128), 0, j, i, g2);
            }
            if (getMaxMp() > 0) {
                paintColorBar(getMP() / getMaxMp(), new Color(0, 128, 255, 128), 1, j, i, g2);
            }
        }
        else { //if dead
            if (this == game.getHero()) {
                panel.drawToCell(g2, panel.getImage("rip"), j, i);
            }
        }
    }
}
