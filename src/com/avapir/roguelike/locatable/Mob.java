package com.avapir.roguelike.locatable;

import static com.avapir.roguelike.core.RoguelikeMain.BORG;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.battle.Battle;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.GamePanel;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.game.ai.AI;
import com.avapir.roguelike.game.ai.SlimeAI;

public class Mob implements Locatable {

	public static final class MobSet {

		private static final Mob	slime	= new Mob(-1, -1, 15, 0, new Attack(2), new Armor(0),
													new SlimeAI(), "Slime");

		public static Mob getSlime() {
			Mob m = new Mob(slime);
			return m;
		}

	}

	private static final class Borg implements AI {

		@Override
		public void computeAI(final Mob m, final Game g) {
			if (!g.isOver()) {
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

	}

	// {
	// mobID = mobs++;
	// }
	// public final int mobID;
	// private static int mobs = 0;

	private final String			name;
	private final AI				intel;

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
			final Armor bDef, final AI ai, final String nm) {
		name = nm;
		intel = ai;

		baseAttack.addAttack(bAtk);
		baseArmor.addArmor(bDef);

		HP = hp;

		maxHP = HP;
		maxMP = MP;

		X = x;
		Y = y;
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
	public Mob(final int x, final int y, final AI ai, final String n, final Map m) {
		intel = BORG ? new Borg() : ai != null ? ai : new AI() {
			@Override
			public void computeAI(final Mob m, final Game g) {}
		};

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
		X = m.X;
		Y = m.Y;
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
		Tile t = g.getMap().getTile(nx, ny);
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
						g.log("Здесь есть " + t.getItemList().get(0).getName() + ".");
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

		Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
		if (defender != g.getHero() && this != g.getHero()) { return 0; }
		float damage = Battle.computeDamage(getAttack(), defender.getArmor());
		defender.receiveDamage(damage, g);

		g.log(String.format("%s наносит %s урона по %s", this.getName(), damage, defender.getName()));
		g.log(String.format("У %s осталось %s здоровья", defender.getName(),
				GamePanel.roundOneDigit(defender.getHP())));

		if (defender.getHP() <= 0) {
			damage -= defender.getHP() * 2;// bonus XP for Overkills
		}

		return damage;
	}

	private void receiveDamage(final float dmg, Game g) {
		HP -= dmg;
		if (HP <= 0) {
			HP = 0;
			g.getMap().removeCharacter(X, Y);
			if (this == g.getHero()) {
				g.gameOver();
				g.repaint();
			}
		}
	}

	public Armor getArmor() {
		return baseArmor;
	}

	public float getArmor(final int i) {
		return getArmor().getArmor(i);
	}

	public Attack getAttack() {
		return baseAttack;
	}

	public float getAttack(final int i) {
		return getAttack().getDamageOfType(i);
	}

	/**
	 * For status-bars
	 */
	public float getMaxHp() {
		return maxHP;
	}

	/**
	 * For status-bars
	 * 
	 * @return
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

	private int	X;
	private int	Y;

	@Override
	public int getX() {
		return X;
	}

	@Override
	public int getY() {
		return Y;
	}

	@Override
	public void setLocation(final int x, final int y) {
		X = x;
		Y = y;
	}

}
