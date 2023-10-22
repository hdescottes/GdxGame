package com.gdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.map.worldMap.Topple;
import com.gdx.game.map.worldMap.ToppleRoad1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class MapFactoryTest {

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

    @ParameterizedTest
    @MethodSource("getMap")
    void testGetMap(MapFactory.MapType mapType, Class<?> mapClass) {
        Map map = MapFactory.getMap(mapType);

        assertThat(map).isInstanceOf(mapClass);
        assertThat(MapFactory.getMapTable()).contains(entry(mapType, map));
    }

    private static Stream<Arguments> getMap() {
        return Stream.of(
                Arguments.of(MapFactory.MapType.TOPPLE, Topple.class),
                Arguments.of(MapFactory.MapType.TOPPLE_ROAD_1, ToppleRoad1.class)
        );
    }

    @Test
    public void testClearCache_ShouldSucceed() {
        MapFactory.getMap(MapFactory.MapType.TOPPLE);
        assertThat(MapFactory.getMapTable()).containsKey(MapFactory.MapType.TOPPLE);

        MapFactory.clearCache();

        assertThat(MapFactory.getMapTable()).isEmpty();
    }
}
