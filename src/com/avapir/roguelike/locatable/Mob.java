package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.battle.Battle;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.game.ai.AbstractAI;
import com.avapir.roguelike.game.ai.SlimeAI;

import java.awt.*;
import java.util.List;

public class Mob implements Locatable {

    protected final Attack       attack;
    protected final Armor        armor;
    // {
    // mobID = mobs++;
    // }
    // public final int mobID;
    // private static int mobs = 0;
    private final   String       name;
    private final   AbstractAI   ai;
    protected       float        HP;
    protected       float        MP;
    protected       float        maxMP;
    protected       float        maxHP;
    private         List<Effect> effects;//TODO AppliedEffect#updateAttack(Attack) #updateArmor(Armor)
    private boolean alive = true;
    private Point location;

    public Mob(String name, float maxHP, float maxMP, Attack bAtk, Armor bArm, AbstractAI ai, Point location) {
        this.name = name;
        this.maxHP = maxHP;
        this.maxMP = maxMP;
        HP = maxHP;
        MP = maxMP;
        attack = new Attack(bAtk);
        armor = new Armor(bArm);
        this.ai = ai;
        this.location = new Point(location);

        //   this.ai = BORG ? new Borg() : ai != null ? ai : IdleAI.getNewInstance();

//            if (m != null && m.hasTile(x, y)) {
//                m.putCharacter(this, x, y);
//            }
    }

    public Object clone() {
        return new Mob(name, maxHP, maxMP, attack, armor, ai, location);
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

        final int ny = getY() + dp.y;
        final int nx = getX() + dp.x;
        final Tile t = g.getMap().getTile(nx, ny);
        if (t != null) {
            if (t.getMob() != null) {
                final float dmg = attackMob(new Point(nx, ny), g);
                if (this == g.getHero()) {
                    ((Hero) this).gainXPfromDamage(dmg, g);
                }
                return new Point(0, 0);
            } else if (t.isPassable()) {
                m.putCharacter(this, nx, ny);
                if (this == g.getHero()) {
                    switch (t.getItemList().size()) {
                        case 1:
                            g.log(String.format("Здесь есть %s.", t.getItemList().get(0).getItem().getName()));
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

        g.log(String.format("%s наносит %s урона по %s", this.getName(), damage, defender.getName()));
        g.log(String.format("У %s осталось %s здоровья", defender.getName(), AbstractGamePanel.roundOneDigit(defender
                                                                                                                     .getHP())));

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

    public void doTurnEffects() {
        for (int i = 0; i < effects.size(); i++) {
            final Effect e = effects.get(i);
            if (e.getAndDecrementTime() == 0 || !e.isAppliedForAll() && this instanceof Hero) {
                e.onRemove(this);
                effects.remove(i);
            } else {
                e.applyTo(this);
            }
        }
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

    public static final class MobSet {

        private static final Mob slime = new Mob("Slime", 15, 0, new Attack(2), new Armor(0), new SlimeAI(),
                                                 new Point(-1, -1));

        public static Mob getSlime() {
            return (Mob) slime.clone();
        }

    }

}
