package com.avapir.roguelike.locatable;

import static com.avapir.roguelike.core.RoguelikeMain.BORG;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.battle.Battle;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.game.ai.AbstractAI;
import com.avapir.roguelike.game.ai.EasyAI;
import com.avapir.roguelike.game.ai.IdleAI;
import com.avapir.roguelike.game.ai.SlimeAI;

public class Mob implements Locatable {

	public static final class MobSet {

		private static final Mob	slime	= new Mob(-1, -1, 15, 0, new Attack(2), new Armor(0),
													new SlimeAI(), "Slime");

		public static Mob getSlime() {
			final Mob m = new Mob(slime);
			return m;
		}

	}

	private static final class Borg extends EasyAI {
		
		static {
			instance = new Borg();
		}

		@Override
		public void computeAI(final Mob m, final Game g) {
			if (g.getState() != GameState.GAME_OVER) {
				if (m == g.getHero()) {
					// Hero h = (Hero) m;
					// final int fovRad = Hero.StatsFormulas.getFOVR(h);
					// final int minX = h.getX() - fovRad;
					// final int maxX = h.getX() + fovRad;
					// final int minY = h.getY() - fovRad;
					// final int maxY = h.getY() + fovRad;
					// цикл запили

				}
			}
		}

		@Override
		public void onDeath(Mob mob, Game g) {
			// TODO Auto-generated method stub

		}

	}

	// {
	// mobID = mobs++;
	// }
	// public final int mobID;
	// private static int mobs = 0;

	private final String			name;
	private final AbstractAI		intel;

	protected final List<Effect>	effects		= new ArrayList<>();
	protected final Attack			baseAttack	= new Attack();
	protected final Armor			baseArmor	= new Armor();

	protected float					HP;
	protected float					MP;
	protected float					maxMP;
	protected float					maxHP;

	/**
	 * Constructor used into {@link MobSet} to create fully described monsters.
	 * 
	 * @param x
	 * @param y
	 * @param hp
	 * @param mp
	 * @param bAtk
	 * @param bDef
	 * @param ai
	 * @param nm
	 */
	private Mob(final int x, final int y, final int hp, final int mp, final Attack bAtk,
			final Armor bDef, final AbstractAI ai, final String nm) {
		name = nm;
		intel = ai;

		baseAttack.addAttack(bAtk);
		baseArmor.addArmor(bDef);

		HP = hp;

		maxHP = HP;
		maxMP = MP;

		location = new Point(x, y);
	}

	/**
	 * Public constructor
	 * 
	 * @param x
	 * @param y
	 * @param ai
	 * @param n
	 * @param m
	 */
	public Mob(final int x, final int y, final AbstractAI ai, final String n, final Map m) {
		intel = BORG ? new Borg() : ai != null ? ai : IdleAI.getNewInstance();

		name = n;

		if (m != null && m.hasTile(x, y)) {
			m.putCharacter(this, x, y);
		}
	}

	/**
	 * Copying constructor
	 * 
	 * @param m
	 */
	private Mob(final Mob m) {
		location.setLocation(location);
		baseArmor.addArmor(baseArmor);
		baseAttack.addAttack(m.baseAttack);
		effects.addAll(m.effects);
		HP = m.HP;
		MP = m.MP;
		maxHP = m.maxHP;
		maxMP = m.maxMP;
		intel = m.intel;
		name = m.name;
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
		Game.checkStep(dp);
		if (dp.x == 0 && dp.y == 0) { return null; }
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
						g.log(String.format("Здесь есть %s.", t.getItemList().get(0).getName()));
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

	protected float attackMob(final Point dp, final Game g) {
		final Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
		if (defender != g.getHero() && this != g.getHero()) { return 0; }
		float damage = Battle.computeDamage(getAttack(), defender.getArmor());
		defender.receiveDamage(damage, g);

		g.log(String.format("%s наносит %s урона по %s", this.getName(), damage, defender.getName()));
		g.log(String.format("У %s осталось %s здоровья", defender.getName(),
				AbstractGamePanel.roundOneDigit(defender.getHP())));

		if (defender.getHP() <= 0) {
			damage -= defender.getHP() * 2;// bonus XP for Overkills
		}

		return damage;
	}

	/**
	 * Decreases damage and checks if this {@link Mob} died
	 * 
	 * @param dmg
	 *            caused damage
	 * @param m
	 *            game where it happened
	 */
	private void receiveDamage(final float dmg, final Game g) {
		HP -= dmg;
		if (HP <= 0) {
			onDeath(g);
		}
	}

	/**
	 * Called once while mob`s hp goes below zero
	 * 
	 * @param g
	 */
	protected void onDeath(final Game g) {
		intel.onDeath(this, g);
	}

	/**
	 * @return native and given by effects {@link Armor}
	 */
	public Armor getArmor() {
		return baseArmor;
	}

	/**
	 * @param i
	 * @return specified type of {@link Armor}
	 */
	public float getArmor(final int i) {
		return getArmor().getArmor(i);
	}

	/**
	 * @return @return native and given by effects {@link Attack}
	 */
	public Attack getAttack() {
		return baseAttack;
	}

	/**
	 * @param i
	 * @return specified type of {@link Attack}
	 */
	public float getAttack(final int i) {
		return getAttack().getDamageOfType(i);
	}

	/**
	 * @return Maximum value of {@link #HP} that this {@link Hero} may have
	 */
	public float getMaxHp() {
		return maxHP;
	}

	/**
	 * @return Maximum value of {@link #MP} that this {@link Hero} may have
	 */
	public float getMaxMp() {
		return maxMP;
	}

	public void doAI(final Game g) {
		intel.computeAI(this, g);
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

	/* Implementation of Locatable interface */

	private Point	location	= new Point(-1, -1);

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

}
