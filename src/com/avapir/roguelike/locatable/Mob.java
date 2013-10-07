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

	static final class MobSet {

		private static final Mob	slime	= new Mob(-1, -1, 15, 0, new Attack(2), new Armor(0),
													new SlimeAI(), "Slime");

		public static Mob getSlime() {
			return new Mob(slime);
		}
	}

	private static final class Borg implements AI {

		@Override
		public void computeAI(final Mob m, Game g) {
			// TODO Auto-generated method stub

		}

	}

	{
		mobID = mobs++;
	}
	public final int				mobID;
	private static int				mobs	= 0;

	protected final List<Effect>	effects;

	private double					HP;
	private double					MP;
	private Attack					baseAttack;
	private Armor					baseArmor;

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

	public Mob(final int x, final int y, final AI ai, final String n, Map m) {
		intel = BORG ? new Borg() : ai != null ? ai : new AI() {

			@Override
			public void computeAI(Mob m, Game g) {
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

	public double getHP() {
		return HP;
	}

	public double getMP() {
		return MP;
	}

	public boolean move(final Point dp, Game g) {
		Game.checkStep(dp);
		if (dp.x == 0 && dp.y == 0) { return false; }
		final Map m = g.getMap();

		final int ny = getY() + dp.y;
		final int nx = getX() + dp.x;
		if (m.hasTile(nx, ny)) {
			if (m.getTile(nx, ny).getMob() != null) {
				float dmg = attackMob(new Point(nx, ny), g);
				if (mobID == 0) g.getHero().gainXPfromDamage(dmg);
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
			}
			g.repaint();
			return true;
		} else {
			return false;
		}
	}

	protected float attackMob(final Point dp, Game g) {
		final Mob defender = g.getMap().getTile(dp.x, dp.y).getMob();
		final float damage = Battle.computeDamage(getAttack(), defender.getDefence());
		defender.getDamage(damage);
		return damage;
	}

	private void getDamage(final float dmg) {
		HP -= dmg;
	}

	protected Armor getDefence() {
		return baseArmor;
	}

	protected Attack getAttack() {
		return baseAttack;
	}

	public void doAI(Game g) {
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
