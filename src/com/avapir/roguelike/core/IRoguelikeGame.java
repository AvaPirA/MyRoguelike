package com.avapir.roguelike.core;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

import java.awt.*;

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

    int getCurrentX();

    int getCurrentY();

    /**
     * Устанавливает состояния конца игры
     */
    void gameOver();

    Game.GameState getState();

    void setState(Game.GameState state);

    void repaint();

    Mob removeMob(Mob m);

    void setScreenCenterAt(Point p);

    void EOT(Point mapMove);
}
