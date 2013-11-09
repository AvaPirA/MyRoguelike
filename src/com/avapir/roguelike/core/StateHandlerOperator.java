package com.avapir.roguelike.core;

import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;

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
