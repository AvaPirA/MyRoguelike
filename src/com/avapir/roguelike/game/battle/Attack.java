package com.avapir.roguelike.game.battle;

/** Physical<br> Magic<br> Water<br> Fire<br> Lightning<br> Curse */
public class Attack implements Cloneable {

    public static final int     TOTAL_DMG_TYPES = 6;
    private final       float[] damage          = new float[TOTAL_DMG_TYPES];

    public Attack(final float... input) {
        if (input.length > TOTAL_DMG_TYPES) {
            throw new IllegalArgumentException("Unknown attack type");
        }
        System.arraycopy(input, 0, damage, 0, input.length);
    }

    public Attack(Attack bAtk) {
        if (bAtk != null) {
            System.arraycopy(bAtk.damage, 0, damage, 0, TOTAL_DMG_TYPES);
        }
    }

    public static Attack sum(Attack a1, Attack a2) {
        Attack a = new Attack();
        a.addAttack(a1);
        a.addAttack(a2);
        return a;
    }

    public Attack addDamage(final float[] atk) {
        if (atk == null) {
            return this;
        }
        if (atk.length != TOTAL_DMG_TYPES) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
            damage[i] += atk[i];
        }
        return this;
    }

    public Attack addAttack(final Attack atk) {
        if (atk == null) {
            return this;
        }
        addDamage(atk.damage);
        return this;
    }

    public float getDamageOfType(final int index) {
        return damage[index];
    }

    public Attack replaceBy(final Attack attack) {
        if (attack == null) {
            return this;
        }
        System.arraycopy(attack.damage, 0, this.damage, 0, TOTAL_DMG_TYPES);
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            Attack atk = (Attack) super.clone();
            System.arraycopy(damage, 0, atk.damage, 0, TOTAL_DMG_TYPES);
            return atk;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
