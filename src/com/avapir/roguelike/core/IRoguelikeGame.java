package com.avapir.roguelike.core;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;

/**
 * User: Alpen Ditrix
 * Date: 09.11.13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public interface IRoguelikeGame {
    Hero getHero();

    Map getMap();

    int getTurnCounter();

    int X();

    int Y();

    /**
     * Устанавливает состояния конца игры
     */
    void gameOver();

    Game.GameState getState();

    void setGameState(Game.GameState state);
}
