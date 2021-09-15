package com.gdx.game.entities.player;

public class CharacterRecord {

    public final int baseHP;
    public final int baseMP;
    public final int baseAttack;
    public final int baseDefense;

    public final String name;

    public CharacterRecord(int baseHP, int baseMP, int baseAttack, int baseDefense, String name)
    {
        this.baseHP = baseHP;
        this.baseMP = baseMP;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.name = name;
    }

    public static String CHAR_NAME_HUMAN = "Human";
    public static String CHAR_NAME_HUMAN2 = "Human2";

    public static CharacterRecord CHARACTERS[] =
            {
                    new CharacterRecord(2, 2, 4, 4, CHAR_NAME_HUMAN),
                    new CharacterRecord(3, 6, 3, 3, CHAR_NAME_HUMAN2),
            };
}
