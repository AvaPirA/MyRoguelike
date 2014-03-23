package com.avapir.roguelike.game.world.character;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Log;
import com.avapir.roguelike.core.Paintable;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.core.gui.GamePanel;
import com.avapir.roguelike.game.battle.Armor;
import com.avapir.roguelike.game.battle.Attack;
import com.avapir.roguelike.game.battle.Battle;
import com.avapir.roguelike.game.world.Locatable;
import com.avapir.roguelike.game.world.character.ai.AbstractAI;
import com.avapir.roguelike.game.world.character.ai.IdleAI;
import com.avapir.roguelike.game.world.character.ai.SlimeAI;
import com.avapir.roguelike.game.world.map.Map;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;

public class Mob implements Cloneable, Locatable, Paintable {

    protected final Attack     attack;
    protected final Armor      armor;
    private final   String     name;
    private final   AbstractAI ai;
    protected       float      HP;
    protected       float      MP;
    protected       float      maxMP;
    protected       float      maxHP;
    private         Point      location;

    protected Mob(String name,
                  float maxHP,
                  float maxMP,
                  Attack attack,
                  Armor armor,
                  Point location,
                  Map map,
                  AbstractAI ai) {
        this.name = name;
        this.maxHP = maxHP;
        this.maxMP = maxMP;
        HP = maxHP;
        MP = maxMP;
        this.attack = new Attack(attack);
        this.armor = new Armor(armor);
        this.ai = ai;
        this.location = new Point(location);

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

    public String getName() {
        return name;
    }

    public float getHP() {
        return HP;
    }

    public float getMP() {
        return MP;
    }

    /**
     * Tries to move in specified direction
     *
     * @param dp where to try to go
     * @param g  {@link Game} instance
     *
     * @return resulting move of player. If step was successful it will equal to {@code dp}. Otherwise it checks is it
     * restricted by rules (e.g. going into wall) or by some state (paralysed). Then method will return {@code null} or
     * {@code new Point(0,0)} respectively.
     */
    public Point move(final Point dp, final Game g) {
        if (dp.x == 0 && dp.y == 0) {
            return null;
        }

        Point newLoc = new Point(getLoc());
        newLoc.translate(dp.x, dp.y);


        Tile t = g.getMap().getTile(newLoc.x, newLoc.y);
        if (t != null) {
            if (t.getMob() != null) {
                moveAttack(newLoc, g);
                return new Point(0, 0);
            } else {
                if (t.isPassable()) {
                    moveTo(newLoc, g);
                    return dp;
                } else if (t.isClosed() && this == g.getHero()) {
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

    private void moveTo(Point newLoc, Game g) {
        g.getMap().putCharacter(this, newLoc.x, newLoc.y);
        if (this == g.getHero()) {
            Tile t = g.getMap().getTile(newLoc.x, newLoc.y);
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
    }

    /**
     * Character tried to go to {@code newLoc} but there is some hostile
     *
     * @param newLoc there to go
     * @param g      {@link Game} instance
     */
    private void moveAttack(Point newLoc, Game g) {
        final float dmg = attackMob(newLoc, g);
        if (this == g.getHero()) {
            ((Hero) this).gainXpFromDamage(dmg);
        }
    }

    private float attackMob(final Point dp, final Game g) {
        final Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
        if (defender != g.getHero() && this != g.getHero()) {
            return 0;
        }
        float damage = Battle.computeDamage(getAttack(), defender.getArmor());
        defender.receiveDamage(damage, g);

        Log.g("%s наносит %s урона по %s", this.getName(), damage, defender.getName());
        Log.g("У %s осталось %s здоровья", defender.getName(), AbstractGamePanel.roundOneDigit(defender.getHP()));

        if (defender.getHP() <= 0) {
            damage -= defender.getHP() * 2;// bonus XP for Overkills
        }

        return damage;
    }

    private void receiveDamage(final float dmg, final Game g) {
        HP -= dmg;
        if (!isAlive()) {
            onDeath(g);
        }
    }

    protected void onDeath(final Game g) {
        ai.onDeath(this, g);
    }

    public boolean isAlive() {
        return HP >= 0;
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

    public void doAI(final Game g) {
        ai.computeAI(this, g);
    }

    @Deprecated
    @Override
    public int getX() {
        return location.x;
    }

    @Deprecated
    @Override
    public int getY() {
        return location.y;
    }

    @Override
    public void setLocation(final int x, final int y) {
        location.setLocation(x, y);
    }

    @Override
    public void setLocation(final Point p) {
        location.setLocation(p);
    }

    @Override
    public Point getLoc() {
        return location;
    }

    @Override
    public String toString() {
        return name + String.format(" (%s, %s)", location.x, location.y);
    }

    @Override
    public Object clone() {
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

    public void paint(AbstractGamePanel panel, Graphics2D g2, int j, int i) {
        if (isAlive()) {
            panel.drawToCell(g2, panel.getImage(getName().toLowerCase()), j, i);
            paintColorBar(getHP() / getMaxHp(), new Color(255, 0, 0, 128), 0, j, i, g2);
            if (getMaxMp() > 0) {
                paintColorBar(getMP() / getMaxMp(), new Color(0, 128, 255, 128), 1, j, i, g2);
            }
        }
    }

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

}