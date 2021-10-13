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

    public static String CHAR_NAME_WARRIOR = "Warrior";
    public static String CHAR_NAME_MAGE = "Mage";
    public static String CHAR_NAME_ROGUE = "Rogue";
    public static String CHAR_NAME_GENERIC = "Generic";
    public static String CHAR_NAME_ENGINEER = "Engineer";

    public static CharacterRecord CHARACTERS[] =
            {
                    new CharacterRecord(2, 2, 4, 4, CHAR_NAME_WARRIOR),
                    new CharacterRecord(3, 6, 3, 3, CHAR_NAME_MAGE),
                    new CharacterRecord(3, 6, 3, 3, CHAR_NAME_ROGUE),
                    new CharacterRecord(3, 6, 3, 3, CHAR_NAME_GENERIC),
                    new CharacterRecord(3, 6, 3, 3, CHAR_NAME_ENGINEER)

            };
}
