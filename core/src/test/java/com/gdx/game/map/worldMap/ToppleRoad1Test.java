package com.gdx.game.map.worldMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.map.MapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Disabled
@ExtendWith(GdxRunner.class)
public class ToppleRoad1Test {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testToppleRoad1_ShouldSucceed() {
        ToppleRoad1 toppleRoad1 = new ToppleRoad1();

        assertThat(toppleRoad1.getMusicTheme()).isNotNull();
        assertThat(toppleRoad1.getCollisionLayer()).isNotNull();
        assertThat(toppleRoad1.getEnemySpawnLayer()).isNotNull();
        assertThat(toppleRoad1.getMapQuestEntities()).isEmpty();
        assertThat(toppleRoad1.getQuestDiscoverLayer()).isNull();
        assertThat(toppleRoad1.getQuestItemSpawnLayer()).isNull();
        assertThat(toppleRoad1.getPortalLayer()).isNotNull();
        assertThat(toppleRoad1.getPlayerStart()).isNotNull();
        assertThat(toppleRoad1.getCurrentMapType()).isEqualTo(MapFactory.MapType.TOPPLE_ROAD_1);
        assertThat(toppleRoad1.getMapEntities().size).isEqualTo(1);
    }
}
