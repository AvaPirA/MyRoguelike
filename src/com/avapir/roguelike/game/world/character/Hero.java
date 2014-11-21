package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.GameStateManager;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.avapir.roguelike.game.world.items.DroppedItem;
import com.avapir.roguelike.game.world.map.MapHolder;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;
import java.util.Iterator;

/**
 * This class implements special type of mob with stats, inventory and equipment. Accordingly,
 * this means that this mob has greater secondary stats, may drop some items and has greater secondary stats again.
 * Also it handles few cases for main hero (e.g. game must end if main hero is dead).
 */
public class Hero extends Mob {

    private static final Hero instance = new Hero("Hero");
    /** {@link InventoryHandler} instance. Responsible for hero's items storage */
    private final InventoryHandler inventory;
    /** {@link EquipmentHandler} instance. Responsible for items that hero equipped */
    private final EquipmentHandler equipment;
    /** {@link PrimaryStats} instance. Responsible for stats of hero that may be found in the calculation formulas */
    private final PrimaryStats     stats;
    /** Amounts of XP needed to level up */
    private int LVL_UP    = 69;
    private int LVL_START = 0;
    /** Current hero's level */
    private int level;
    /** Current amount of experience */
    private int XP;

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

    public static Hero getInstance() { return instance;}

    public EquipmentHandler getEquipment() {
        return equipment;
    }

    public InventoryHandler getInventory() {
        return inventory;
    }

    public PrimaryStats getStats() {
        return stats;
    }

    public int getXP() {
        return XP;
    }

    public int getLevel() {
        return level;
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

        while (XP >= LVL_UP) {
            lvlUp();
        }
    }

    public int getAdvanceXP() {
        return LVL_UP;
    }

    public int getPrevLevelXp() {
        return LVL_START;
    }

    /**
     * Puts items from tile on which hero stands to inventory (while it has free space)
     */
    public void pickUpItems() {
        java.util.List<DroppedItem> items = MapHolder.getInstance().getTile(getLoc().x, getLoc().y).getItemList();
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

    private void lvlUp() {
        LVL_START = LVL_UP;
        LVL_UP += XP;
        level++;
        stats.defaultIncrease();
        restore();
        Log.g("%s достиг %s уровня!", getName(), level);
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
    public Attack getAttack() {
        return Attack.sum(super.getAttack(), equipment.getAttack());
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
