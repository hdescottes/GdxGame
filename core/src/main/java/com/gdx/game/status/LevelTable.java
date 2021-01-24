package com.gdx.game.status;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.stream.Stream;

public class LevelTable {
    private String levelID;
    private int xpMax;
    private int hpMax;
    private int mpMax;

    public String getLevelID() {
        return levelID;
    }

    public void setLevelID(String levelID) {
        this.levelID = levelID;
    }

    public int getXpMax() {
        return xpMax;
    }

    public void setXpMax(int xpMax) {
        this.xpMax = xpMax;
    }

    public int getHpMax() {
        return hpMax;
    }

    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    public int getMpMax() {
        return mpMax;
    }

    public void setMpMax(int mpMax) {
        this.mpMax = mpMax;
    }

    static public Array<LevelTable> getLevelTables(String configFilePath){
        Json json = new Json();
        Array<LevelTable> levelTable = new Array<>();

        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));
        list.forEach(v -> levelTable.add(json.readValue(LevelTable.class, v)));

        return levelTable;
    }
}
