package com.avapir.roguelike.game.ai;

public abstract class EasyAI extends AbstractAI {

	static EasyAI	instance;

	public static EasyAI getNewInstance() {
		return instance;
	}

}
