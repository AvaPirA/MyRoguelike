package com.avapir.roguelike.core;

import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;

/**
 * Describes all states which must be operated in my game. <br>
 * All handlers except KeyboardHandler must be created and removed than needed. Of course, also it must be able to #get
 * him.
 *
 * @author Alpen Ditrix
 * @since 0.0.1
 */
interface StateManager {

    /* Creators */
    void createStatsHandler();

    void createInventoryHandler();

    /* Getters */

    ChangingStatsHandler getStatsHandler();

    InventoryHandler getInventoryHandler();

    /* Removers */
    void removeInventoryHandler();

    void removeStatsHandler();

}
