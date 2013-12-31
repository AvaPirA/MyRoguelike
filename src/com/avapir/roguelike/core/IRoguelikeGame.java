package com.avapir.roguelike.core;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

import java.awt.*;

public interface IRoguelikeGame {
    Hero getHero();

    Map getMap();

    int getTurnCounter();

    int getCurrentX();

    int getCurrentY();

    void gameOver();

    Game.GameState getState();

    void setState(Game.GameState state);

    void repaint();

    Mob removeMob(Mob m);

    /**
     * End of turn
     *
     * @param mapMove step did by {@link Hero} character
     */
    void EOT(Point mapMove);
}
