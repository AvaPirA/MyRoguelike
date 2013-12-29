package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.battle.Battle;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.game.ai.AbstractAI;
import com.avapir.roguelike.game.ai.IdleAI;
import com.avapir.roguelike.game.ai.SlimeAI;

import java.awt.*;

public class Mob implements Cloneable, Locatable {

    public static   Game       game;
    protected final Attack     attack;
    protected final Armor      armor;
    private final   String     name;
    private final   AbstractAI ai;
    protected       float      HP;
    protected       float      MP;
    protected       float      maxMP;
    protected       float      maxHP;
    private boolean alive = true;
    private Point location;

    protected Mob(String name, float maxHP, float maxMP, Attack attack, Armor armor, Point location, AbstractAI ai) {
        this.name = name;
        this.maxHP = maxHP;
        this.maxMP = maxMP;
        HP = maxHP;
        MP = maxMP;
        this.attack = new Attack(attack);
        this.armor = new Armor(armor);
        this.ai = ai;
        this.location = new Point(location);

        Map m = game.getMap();
        if (m != null && m.hasTile(location.x, location.y)) {
            m.putCharacter(this, location.x, location.y);
        }
    }

    public static final class MobBuilder {

        public static final int DFT_HP = 100;
        public static final int DFT_MP = 100;

        public static Mob createMob(String name) {
            return createMob(name, UNRESOLVED_LOCATION);
        }

        public static Mob createMob(String name, Point location) {
            return createMob(name, DFT_HP, DFT_MP, location);
        }

        public static Mob createMob(String name, Attack attack, Armor armor) {
            return createMob(name, attack, armor, UNRESOLVED_LOCATION);
        }

        public static Mob createMob(String name, float maxHP, float maxMP) {
            return createMob(name, maxHP, maxMP, UNRESOLVED_LOCATION);
        }

        public static Mob createMob(String name, float maxHP, float maxMP, Point location) {
            return createMob(name, maxHP, maxMP, new Attack(), new Armor(), location);
        }

        public static Mob createMob(String name, Attack attack, Armor armor, Point location) {
            return createMob(name, DFT_HP, DFT_MP, attack, armor, location);
        }

        public static Mob createMob(String name, float maxHP, float maxMP, Attack attack, Armor armor) {
            return createMob(name, maxHP, maxMP, attack, armor, UNRESOLVED_LOCATION);
        }

        public static Mob createMob(String name, float maxHP, float maxMP, Attack attack, Armor armor, Point location) {
            return createMob(name, maxHP, maxMP, attack, armor, location, IdleAI.getNewInstance());

        }

        public static Mob createMob(String name,
                                    float maxHP,
                                    float maxMP,
                                    Attack attack,
                                    Armor armor,
                                    Point location,
                                    AbstractAI ai) {
            return new Mob(name, maxHP, maxMP, attack, armor, location, ai);
        }

        public static Mob createMob(String name, int maxHP, int maxMP, Attack attack, Armor armor, AbstractAI ai) {
            return createMob(name, maxHP, maxMP, attack, armor, UNRESOLVED_LOCATION, ai);
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

    public Point move(final Point dp, final Game g) {
        if (dp.x == 0 && dp.y == 0) {
            return null;
        }
        final Map m = g.getMap();

        final int ny = getLoc().y + dp.y;
        final int nx = getLoc().x + dp.x;
        final Tile t = g.getMap().getTile(nx, ny);
        if (t != null) {
            if (t.getMob() != null) {
                final float dmg = attackMob(new Point(nx, ny), g);
                if (this == g.getHero()) {
                    ((Hero) this).gainXpFromDamage(dmg, g);
                }
                return new Point(0, 0);
            } else if (t.isPassable()) {
                m.putCharacter(this, nx, ny);
                if (this == g.getHero()) {
                    switch (t.getItemsAmount()) {
                        case 1:
                            g.logFormat("Здесь есть %s.", t.getItemList().get(0).getItem().getName());
                        case 0:
                            break;
                        default:
                            g.log("Здесь лежит много вещей.");
                    }
                }
            } else if (t.isClosed() && this == g.getHero()) {
                // g.TryToOpen(ny, nx, true);
                return new Point(0, 0);
            } else {
                return new Point(0, 0);
            }
            return dp;
        } else {
            return null;
        }
    }

    float attackMob(final Point dp, final Game g) {
        final Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
        if (defender != g.getHero() && this != g.getHero()) {
            return 0;
        }
        float damage = Battle.computeDamage(getAttack(), defender.getArmor());
        defender.receiveDamage(damage, g);

        g.logFormat("%s наносит %s урона по %s", this.getName(), damage, defender.getName());
        g.logFormat("У %s осталось %s здоровья", defender.getName(), AbstractGamePanel.roundOneDigit(defender.getHP()));

        if (defender.getHP() <= 0) {
            damage -= defender.getHP() * 2;// bonus XP for Overkills
        }

        return damage;
    }

    private void receiveDamage(final float dmg, final Game g) {
        HP -= dmg;
        if (HP <= 0) {
            onDeath(g);
        }
    }

    void onDeath(final Game g) {
        alive = false;
        ai.onDeath(this, g);
    }

    public boolean isAlive() {
        return alive;
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

}
