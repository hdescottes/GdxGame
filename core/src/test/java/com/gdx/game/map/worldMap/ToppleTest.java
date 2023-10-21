package com.gdx.game.map.worldMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.map.MapFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class ToppleTest {

    private MockedConstruction<NPCGraphicsComponent> mockNPCGraphics;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockNPCGraphics = mockConstruction(NPCGraphicsComponent.class);
    }

    @AfterEach
    void end() {
        mockNPCGraphics.close();
    }

    @Test
    public void testTopple_ShouldSucceed() {
        Topple topple = new Topple();

        assertThat(topple.getMusicTheme()).isNotNull();
        assertThat(topple.getCollisionLayer()).isNotNull();
        assertThat(topple.getEnemySpawnLayer()).isNull();
        assertThat(topple.getMapQuestEntities()).isEmpty();
        assertThat(topple.getQuestDiscoverLayer()).isNull();
        assertThat(topple.getQuestItemSpawnLayer()).isNotNull();
        assertThat(topple.getPortalLayer()).isNotNull();
        assertThat(topple.getPlayerStart()).isNotNull();
        assertThat(topple.getCurrentMapType()).isEqualTo(MapFactory.MapType.TOPPLE);
        assertThat(topple.getMapEntities().size).isEqualTo(5);
    }
}
