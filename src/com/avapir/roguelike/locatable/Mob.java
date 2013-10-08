package com.avapir.roguelike.locatable;

import static com.avapir.roguelike.core.RoguelikeMain.BORG;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.battle.Battle;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.ai.AI;
import com.avapir.roguelike.game.ai.SlimeAI;

public class Mob implements Locatable {

	public static final class MobSet {

		private static final Mob	slime	= new Mob(-1, -1, 15, 0, new Attack(2), new Armor(0),
													new SlimeAI(), "Slime");

		public static Mob getSlime() {
			return new Mob(slime);
		}
	}

	private static final class Borg implements AI {

		@Override
		public void computeAI(final Mob m, final Game g) {
			// TODO Auto-generated method stub

		}

	}

	{
		mobID = mobs++;
	}
	public final int				mobID;
	private static int				mobs		= 0;

	protected final List<Effect>	effects;

	protected float					HP			= 0;
	protected float					MP			= 0;
	protected Attack				baseAttack	= new Attack();
	protected Armor					baseArmor	= new Armor();

	private final AI				intel;
	public final String				name;

	private Mob(final int x, final int y, final int hp, final int mp, final Attack bAtk,
			final Armor bDef, final AI ai, final String n) {
		X = x;
		Y = y;
		HP = hp;
		MP = mp;
		baseAttack = bAtk;
		baseArmor = bDef;
		intel = ai;
		name = n;
		effects = new ArrayList<>();
	}

	public Mob(final int x, final int y, final AI ai, final String n, final Map m) {
		intel = BORG ? new Borg() : ai != null ? ai : new AI() {

			@Override
			public void computeAI(final Mob m, final Game g) {
				// TODO Auto-generated method stub

			}
		};
		name = n;
		effects = new ArrayList<>();
		if (m != null && m.hasTile(x, y)) {
			m.putCharacter(this, x, y);
		}
	}

	private Mob(final Mob m) {
		X = m.X;
		Y = m.Y;
		baseArmor = m.baseArmor;
		baseAttack = m.baseAttack;
		effects = new ArrayList<>();
		effects.addAll(m.effects);
		HP = m.HP;
		MP = m.MP;
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
		if (m.hasTile(nx, ny)) {
			if (m.getTile(nx, ny).getMob() != null) {
				final float dmg = attackMob(new Point(nx, ny), g);
				if (mobID == 0) {
					g.getHero().gainXPfromDamage(dmg, g);
				}
				return new Point(0,0);
			} else if (m.getTile(nx, ny).isPassable()) {
				m.putCharacter(this, nx, ny);
				if (this.mobID == 0) {
					switch (m.getTile(nx, ny).getItemList().size()) {
					case 1:
						g.log("Здесь есть "
								+ m.getTile(nx, ny).getItemList().get(0).getName().toLowerCase()
								+ ".");
					case 0:
					break;
					default:
						g.log("Здесь лежит много вещей.");
					}
				}
			} else if (m.getTile(nx, ny).isClosed() && mobID == 0) {
				// g.TryToOpen(ny, nx, true);
				return new Point(0,0);
			}
			return dp;
		} else {
			return null;
		}
	}

	protected float attackMob(final Point dp, final Game g) {
		final Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
		float damage = Battle.computeDamage(getAttack(), defender.getDefence());
		defender.getDamage(damage);
		g.log(this.getName() + " наносит " + damage + " урона по " + defender.getName());
		g.log("У " + defender.getName() + " осталось "
				+ com.avapir.roguelike.core.GamePanel.roundOneDigit(defender.getHP()) + " здоровья");
		if (defender.getHP() <= 0) {
			damage -= defender.getHP() * 2;// bonus XP for Overkills
			g.getMap().removeCharacter(defender.getX(), defender.getY());
		}
		if (this.getHP() <= 0) {
			g.getMap().removeCharacter(X, Y);
		}
		return damage;
	}

	private void getDamage(final float dmg) {
		HP -= dmg;
	}

	public Armor getDefence() {
		return baseArmor;
	}

	public float getDefence(final int i) {
		return getDefence().getArmor(i);
	}

	public Attack getAttack() {
		return baseAttack;
	}

	public float getAttack(final int i) {
		return getAttack().getDamage(i);
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
