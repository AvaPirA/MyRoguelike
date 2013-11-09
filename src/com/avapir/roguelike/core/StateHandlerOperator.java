package com.avapir.roguelike.core;

import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;

/**
 * User: Alpen Ditrix
 * Date: 09.11.13
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */
public interface StateHandlerOperator {
    ChangingStatsHandler getStatsHandler();

    InventoryHandler getInventoryHandler();

    void createStatsHandler();

    void removeStatsHandler();

    void createInventoryHandler();

    void removeInventoryHandler();

    KeyboardHandler getKeyboardHandler();

    void setKeyboardHandler(KeyboardHandler keyboardHandler);
}
