package com.gdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.npc.NPCInputComponent;
import com.gdx.game.entities.player.PlayerInputComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class EntityFactoryTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testGetInstance_ShouldSucceed() {
        EntityFactory entityFactory = EntityFactory.getInstance();

        assertThat(entityFactory).isNotNull();
    }

    @Test
    public void testGetEntity_ShouldSucceedWithPlayer() {
        Entity entity = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER);

        assertThat(entity).isNotNull();
        assertThat(entity.getEntityConfig().getEntityID()).isEqualTo(Entity.getEntityConfig(EntityFactory.PLAYER_CONFIG).getEntityID());
        assertThat(entity.getInputProcessor()).isInstanceOf(PlayerInputComponent.class);
    }

    @Test
    public void testGetEntity_ShouldSucceedWithNPC() {
        Entity entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC);

        assertThat(entity).isNotNull();
        assertThat(entity.getInputProcessor()).isInstanceOf(NPCInputComponent.class);
    }

    @Test
    public void testGetEntityByName_ShouldSucceed() {
        Entity entity = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_BLACKSMITH);

        assertThat(entity).isNotNull();
        assertThat(entity.getInputProcessor()).isInstanceOf(NPCInputComponent.class);
    }
}
