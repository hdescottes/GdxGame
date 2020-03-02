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
        WALKING_UP,
        WALKING_DOWN,
        WALKING_RIGHT,
        WALKING_LEFT,
        LOOK_UP,
        LOOK_DOWN,
        LOOK_RIGHT,
        LOOK_LEFT,
        FLYING,
        HOVERING,
        LANDING
    }
}
