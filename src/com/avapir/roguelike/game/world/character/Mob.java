package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.Drawable;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.core.gui.GamePanel;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.avapir.roguelike.game.battle.Battle;
import com.avapir.roguelike.game.world.Locatable;
import com.avapir.roguelike.game.world.character.ai.AbstractAI;
import com.avapir.roguelike.game.world.character.ai.IdleAI;
import com.avapir.roguelike.game.world.character.ai.SlimeAI;
import com.avapir.roguelike.game.world.items.Item;
import com.avapir.roguelike.game.world.map.Map;
import com.avapir.roguelike.game.world.map.MapHolder;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;
import java.util.List;

/**
 * This class represents any actor in the game. Each mob has default stats such as name, max or current HP and MP
 * amount, attack and defence power and current location on the map. Also each mob (or more often -- each kind of mobs)
 * may have so behavior specified by implementation of {@link com.avapir.roguelike.game.world.character.ai.AbstractAI}
 */
public class Mob extends Locatable implements Cloneable, Drawable {

    protected final Attack     attack;
    protected final Armor      armor;
    private final   String     name;
    private final   AbstractAI ai;
    protected       float      HP;
    protected       float      MP;
    protected       float      maxMP;
    protected       float      maxHP;

    protected Mob(String name) {
        this.name = name;
        this.ai = IdleAI.getNewInstance();
        maxHP = 1;
        maxMP = 1;
        HP = maxHP;
        MP = maxMP;
        attack = new Attack();
        armor = new Armor();
    }

    protected Mob(String name,
                  float maxHP,
                  float maxMP,
                  Attack attack,
                  Armor armor,
                  Point location,
                  Map map,
                  AbstractAI ai) {
        super(location);
        this.name = name;
        this.maxHP = maxHP;
        this.maxMP = maxMP;
        HP = maxHP;
        MP = maxMP;
        this.attack = new Attack(attack);
        this.armor = new Armor(armor);
        this.ai = ai;

        if (map != null && map.hasTile(location.x, location.y)) {
            map.putCharacter(this, location.x, location.y);
        }
    }

    public static final class MobBuilder {

        public static final int DFT_HP = 100;
        public static final int DFT_MP = 100;

        public static Mob createMob(String name) {
            return createMob(name, UNRESOLVED_LOCATION, null);
        }

        public static Mob createMob(String name, Point location, Map map) {
            return createMob(name, DFT_HP, DFT_MP, location, map);
        }

        public static Mob createMob(String name, Attack attack, Armor armor) {
            return createMob(name, attack, armor, UNRESOLVED_LOCATION, null);
        }

        public static Mob createMob(String name, float maxHP, float maxMP) {
            return createMob(name, maxHP, maxMP, UNRESOLVED_LOCATION, null);
        }

        public static Mob createMob(String name, float maxHP, float maxMP, Point location, Map map) {
            return createMob(name, maxHP, maxMP, new Attack(), new Armor(), location, map);
        }

        public static Mob createMob(String name, Attack attack, Armor armor, Point location, Map map) {
            return createMob(name, DFT_HP, DFT_MP, attack, armor, location, map);
        }

        public static Mob createMob(String name, float maxHP, float maxMP, Attack attack, Armor armor) {
            return createMob(name, maxHP, maxMP, attack, armor, UNRESOLVED_LOCATION, null);
        }

        public static Mob createMob(String name,
                                    float maxHP,
                                    float maxMP,
                                    Attack attack,
                                    Armor armor,
                                    Point location,
                                    Map map) {
            return createMob(name, maxHP, maxMP, attack, armor, location, map, IdleAI.getNewInstance());

        }

        public static Mob createMob(String name,
                                    float maxHP,
                                    float maxMP,
                                    Attack attack,
                                    Armor armor,
                                    Point location,
                                    Map map,
                                    AbstractAI ai) {
            return new Mob(name, maxHP, maxMP, attack, armor, location, map, ai);
        }

        public static Mob createMob(String name, int maxHP, int maxMP, Attack attack, Armor armor, AbstractAI ai) {
            return createMob(name, maxHP, maxMP, attack, armor, UNRESOLVED_LOCATION, null, ai);
        }
    }

    public static final class MobSet {

        public static Mob getSlime() {
            return MobBuilder.createMob("Slime", 15, 0, new Attack(2), new Armor(0), new SlimeAI());
        }

    }

    public AbstractAI getAi() {
        return ai;
    }

    /**
     * @return in-game name of mob
     */
    public String getName() {
        return name;
    }

    /**
     * @return current HP value of mob
     */
    public float getHP() {
        return HP;
    }

    /**
     * @return current MP value of mob
     */
    public float getMP() {
        return MP;
    }

    /**
     * Moves mob to new location
     *
     * @param newLoc
     */
    protected void moveTo(Point newLoc) {
        MapHolder.getInstance().putCharacter(this, newLoc.x, newLoc.y);
    }

    /**
     * Tries to move in specified direction
     *
     * @param dp where to try to go
     *
     * @return resulting move of player. If step was successful it will equal to {@code dp}. Otherwise it checks is it
     * restricted by rules (e.g. going into wall) or by some state (paralysed). Then method will return {@code null} or
     * {@code new Point(0,0)} respectively.
     */
    public Point move(final Point dp) {
        if (dp.x == 0 && dp.y == 0) {
            return null;
        }

        Point newLoc = new Point(getLoc());
        newLoc.translate(dp.x, dp.y);

        Tile t = MapHolder.getInstance().getTile(newLoc.x, newLoc.y);
        if (t != null) {
            if (t.getMob() != null) {
                moveAttack(newLoc);
                return new Point(0, 0);
            } else {
                if (t.isPassable()) {
                    moveTo(newLoc);
                    return dp;
                } else if (t.isClosed() && this == Hero.getInstance()) {
                    // g.TryToOpen(ny, nx, true);
                    return new Point(0, 0);
                } else {
                    return new Point(0, 0);
                }
            }
        } else { // if no such tile on the map
            return null;
        }
    }

    /**
     * Character tried to go to {@code newLoc} but there is some hostile. Also method returns exp-valued damage
     * amount. For usual hit it equals to dealt damage, but not when defender was killed. All overdealt damage will
     * be plused to "exp-valued damage" twice. <p>E.g.: defender "D" had 10HP and attacker "A" dealt 25 damage. Then
     * "A"
     * will gain 10 exp for "D"`s 10HP and (25-10)*2 exp for not hurted but dealt damage. Summary it's 40 exp. </p>
     * Such way overkills are encouraged.
     *
     * @param newLoc there to go
     *
     * @return amount of damage for which character will gain experience.
     */
    protected float moveAttack(Point newLoc) {
        final Mob defender = MapHolder.getInstance().getTile(newLoc.x, newLoc.y).getMob();
        if (defender != Hero.getInstance() && this != Hero.getInstance()) {
            return 0;
        }
        float damage = Battle.computeDamage(getAttack(), defender.getArmor());
        defender.dealDamage(damage);

        Log.g("%s наносит %s урона по %s", this.getName(), damage, defender.getName());
        Log.g("У %s осталось %s здоровья", defender.getName(), AbstractGamePanel.roundOneDigit(defender.getHP()));

        if (defender.getHP() <= 0) {
            damage += Math.abs(defender.getHP() * 2);// bonus XP for Overkills
        }

        return damage;
    }

    /**
     * Inflicts pure damage and checks if character is alive
     *
     * @param dmg inflicted damage
     */
    private void dealDamage(final float dmg) {
        HP -= dmg;
        if (!isAlive()) {
            onDeath();
        }
    }

    /**
     * Applies some mob-specific things caused by death (important for e.g. bosses)
     */
    protected void onDeath() {
        ai.mustDie(this);
    }

    /**
     * @return {@link Mob#HP} > 0
     */
    public boolean isAlive() {
        return HP > 0;
    }

    Armor getArmor() {
        return armor;
    }

    public float getArmor(final int i) {
        return getArmor().getArmor(i);
    }

    Attack getAttack() {
        return attack;
    }

    public float getAttack(final int i) {
        return getAttack().getDamageOfType(i);
    }

    public float getMaxHp() {
        return maxHP;
    }

    public float getMaxMp() {
        return maxMP;
    }

    public void doAI() {
        ai.computeAI(this);
    }

    @Override
    public String toString() {
        return name + String.format(" (%s, %s)", getLoc().x, getLoc().y);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            Mob mob = (Mob) super.clone();
            mob.attack.replaceBy(attack);
            mob.armor.replaceBy(armor);
            return mob;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void draw(AbstractGamePanel panel, Graphics2D g2, int j, int i) {
        if (isAlive()) {
            panel.drawToCell(g2, panel.getImage(getName().toLowerCase()), j, i);
            paintColorBar(getHP() / getMaxHp(), new Color(255, 0, 0, 128), 0, j, i, g2);
            if (getMaxMp() > 0) {
                paintColorBar(getMP() / getMaxMp(), new Color(0, 128, 255, 128), 1, j, i, g2);
            }
        }
    }

    /**
     * Paints colored bar for some stat of character above him. Usually used for HP\MP. That method receives only
     * percentage value of stat (float value of {@code currentValue/maxValue}).
     *
     * @param value            percents of tile which wil be filled twice
     * @param transparentColor color which will be used
     * @param line             number of line painting already. Usually it's 0 for HP and 1 for MP
     * @param j                horizontal coordinate of tile
     * @param i                vertical coordinate of tile
     * @param g2               {@link Graphics2D} instance
     */
    protected void paintColorBar(final float value,
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
        int y = i * Tile.SIZE_px + line * GamePanel.STAT_BAR_HEIGHT_PX;

        g2.fillRect(x, y, Tile.SIZE_px, GamePanel.STAT_BAR_HEIGHT_PX);
        g2.fillRect(x, y, (int) (value * Tile.SIZE_px), GamePanel.STAT_BAR_HEIGHT_PX);
    }

    public List<Item> getDrop() {
        return ai.getDrop();
    }

}
