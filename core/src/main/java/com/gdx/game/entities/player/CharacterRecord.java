package com.gdx.game.entities.player;

import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.gdx.game.entities.EntityFactory.EntityType.CLERIC;
import static com.gdx.game.entities.EntityFactory.EntityType.GRAPPLER;
import static com.gdx.game.entities.EntityFactory.EntityType.MAGE;
import static com.gdx.game.entities.EntityFactory.EntityType.THIEF;
import static com.gdx.game.entities.EntityFactory.EntityType.WARRIOR;

public class CharacterRecord {

    private final int baseHP;
    private final int baseMP;
    private final int baseAttack;
    private final int baseDefense;
    private final int baseSpeed;
    private final String name;

    public static final List<EntityFactory.EntityType> charactersList = Arrays.asList(WARRIOR, MAGE, THIEF, GRAPPLER,
            CLERIC);

    public static CharacterRecord[] CHARACTERS = charactersList.stream()
            .map(c -> new CharacterRecord(2, 2,
                    loadStats(c.name(), EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS),
                    loadStats(c.name(), EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS),
                    loadStats(c.name(), EntityConfig.EntityProperties.ENTITY_SPEED_POINTS),
                    c.name()))
            .toArray(CharacterRecord[]::new);

    public CharacterRecord(int baseHP, int baseMP, int baseAttack, int baseDefense, int baseSpeed, String name) {
        this.baseHP = baseHP;
        this.baseMP = baseMP;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseSpeed = baseSpeed;
        this.name = name;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseMP() {
        return baseMP;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public String getName() {
        return name;
    }

    private static int loadStats(String charName, EntityConfig.EntityProperties prop) {
        Entity entity = EntityFactory.getInstance().getEntity( EntityFactory.EntityType.valueOf(charName.toUpperCase(Locale.ROOT)));
        return Integer.parseInt(entity.getEntityConfig().getPropertyValue(prop.toString()));
    }
}
