package com.gdx.game.inventory.store;


public interface StoreInventorySubject {
    void addObserver(StoreInventoryObserver storeObserver);
    void removeObserver(StoreInventoryObserver storeObserver);
    void removeAllObservers();
    void notify(String value, StoreInventoryObserver.StoreInventoryEvent event);
}
