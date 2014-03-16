package com.avapir.roguelike.battle;

public class Attack implements Cloneable {

    public static final int     TOTAL_DMG_TYPES = 6;
    private final       float[] damage          = new float[TOTAL_DMG_TYPES];

    public Attack(final float... input) {
        if (input.length > TOTAL_DMG_TYPES) {
            throw new IllegalArgumentException();
        }

        System.arraycopy(input, 0, damage, 0, input.length);
    }

    private Attack() {}

    public Attack(Attack bAtk) {
        if (bAtk != null) {
            System.arraycopy(bAtk.damage, 0, damage, 0, TOTAL_DMG_TYPES);
        }
    }

//    @Override
//    public Object clone() {
//        Attack newInst = new Attack();
//        System.arraycopy(damage, 0, newInst.damage, 0, TOTAL_DMG_TYPES);
//
//    }

    public Attack addDamage(final float[] damages) {
        if (damages.length > TOTAL_DMG_TYPES) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < damages.length; i++) {
            damage[i] += damages[i];
        }
        return this;
    }

    Attack addDamageFromAttack(final Attack atk) {
        if (atk == null) {
            return this;
        }

        for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
            damage[i] += atk.damage[i];
        }
        return this;
    }

    public Attack addAttack(final Attack atk) {
        if (atk == null) {
            return this;
        }
        addDamage(atk.damage);
//        addDamageFromAttack(atk);
        return this;
    }

    public float getDamageOfType(final int index) {
        return damage[index];
    }

    public float[] getDamage() {
        return damage;
    }

    public Attack replaceBy(final Attack attack) {
        if (attack == null) {
            return this;
        }

        clear();
        addAttack(attack);
        return this;
    }

    private void clear() {
        for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
            damage[i] = 0;
        }
    }

    @Override
    public Object clone() {
        try {
            Attack atk = (Attack) super.clone();
            System.arraycopy(damage, 0, atk.damage, 0, TOTAL_DMG_TYPES);
            return atk;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public static Attack sum(Attack a1, Attack a2) {
        Attack a = new Attack();
        a.addAttack(a1);
        a.addAttack(a2);
        return a;
    }
}

