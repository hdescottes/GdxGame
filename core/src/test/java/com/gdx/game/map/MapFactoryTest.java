package com.gdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.map.worldMap.Topple;
import com.gdx.game.map.worldMap.ToppleRoad1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunner.class)
public class MapFactoryTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testGetMap_ShoudSucceedWithTopple() {
        Gdx.app.postRunnable(() -> {
            Map map = MapFactory.getMap(MapFactory.MapType.TOPPLE);

            assertThat(map).isInstanceOf(Topple.class);
            assertThat(MapFactory.getMapTable()).contains(entry(MapFactory.MapType.TOPPLE, map));
        });
    }

    @Test
    public void testGetMap_ShoudSucceedWithToppleRoad1() {
        Gdx.app.postRunnable(() -> {
            Map map = MapFactory.getMap(MapFactory.MapType.TOPPLE_ROAD_1);

            assertThat(map).isInstanceOf(ToppleRoad1.class);
            assertThat(MapFactory.getMapTable()).contains(entry(MapFactory.MapType.TOPPLE_ROAD_1, map));
        });
    }

    @Test
    public void testClearCache_ShoudSucceed() {
        Gdx.app.postRunnable(() -> {
            MapFactory.getMap(MapFactory.MapType.TOPPLE);
            assertThat(MapFactory.getMapTable()).containsKey(MapFactory.MapType.TOPPLE);

            MapFactory.clearCache();

            assertThat(MapFactory.getMapTable()).isEmpty();
        });
    }
}
