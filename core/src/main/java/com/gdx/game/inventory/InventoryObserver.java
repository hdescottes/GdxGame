package com.gdx.game.inventory;

public interface InventoryObserver {
    enum InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
        ITEM_CONSUMED,
        DROP_ITEM_ADDED,
        ADD_WAND_AP,
        REMOVE_WAND_AP,
        REFRESH_STATS,
        NONE
    }

    void onNotify(final String value, InventoryEvent event);
}
