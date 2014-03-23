package com.avapir.roguelike.core;

import com.avapir.roguelike.game.world.character.Hero;
import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.map.Map;

import java.awt.*;

/**
 * Here defined function that inherent in the roguelike game.
 *
 * @author Alpen Ditrix
 * @since 0.0.1
 */
public interface IRoguelikeGame {

    /**
     * @return instance of hero-mob, which is controlled by player (user).
     *
     * @see com.avapir.roguelike.game.world.character.Hero
     */
    Hero getHero();

    /**
     * @return instance of game map
     *
     * @see com.avapir.roguelike.game.world.map.Map
     */
    Map getMap();

    /**
     * Roguelike is a turn-based-game. In each turn hero may do only one move-action.
     *
     * @return turn since game start
     */
    int getTurnCounter();

    /**
     * @return horizontal coordinate of tile at the center of screen
     */
    int getCurrentX();

    /**
     * @return vertical coordinate of tile at the center of screen
     */
    int getCurrentY();

    void gameOver();

    /**
     * @return current state of game
     *
     * @see com.avapir.roguelike.core.Game.GameState
     */
    Game.GameState getState();

    /**
     * Resets previous game state with new one
     *
     * @param state new game state
     *
     * @see com.avapir.roguelike.core.Game.GameState
     */
    void setState(Game.GameState state);

    /**
     * Executes repainting the whole game screen. That method triggers only painting without any state changes.
     */
    void repaint();

    /**
     * @param m
     *
     * @return
     */
    Mob removeMob(Mob m);

    /**
     * End of turn
     *
     * @param mapMove step did by {@link Hero} character
     */
    void EOT(Point mapMove);
}
