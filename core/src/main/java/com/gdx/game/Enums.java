package com.gdx.game;

public class Enums {
    public enum TILETYPE {
        GRASS,
        WATER,
        CLIFF
    }

    public enum ENTITYTYPE {
        HERO,
        TREE,
        BIRD
    }

    public enum ENTITYSTATE {
        NONE,
        IDLE,
        FEEDING,
        WALKING,
        FLYING,
        HOVERING,
        LANDING
    }
}
