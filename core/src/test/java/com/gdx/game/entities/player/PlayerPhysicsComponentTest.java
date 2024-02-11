package com.gdx.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.gdx.game.GdxRunner;
import com.gdx.game.component.Component;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.map.Map;
import com.gdx.game.map.MapFactory;
import com.gdx.game.map.MapManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(GdxRunner.class)
public class PlayerPhysicsComponentTest {

    private MockedConstruction<SpriteBatch> mockSpriteBatch;
    private MockedConstruction<ShapeRenderer> mockShapeRenderer;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockSpriteBatch = mockConstruction(SpriteBatch.class);
        mockShapeRenderer = mockConstruction(ShapeRenderer.class);
    }

    @AfterEach
    void end() {
        mockSpriteBatch.close();
        mockShapeRenderer.close();
    }

    @ParameterizedTest
    @MethodSource("collisionMapData")
    public void testCollisionMapEntities(MapFactory.MapType mapType, Component.MESSAGE message, String entityId) {
        Entity player = spy(EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR));
        MapManager mapManager = new MapManager();
        mapManager.setCamera(new OrthographicCamera());
        mapManager.setPlayer(player);
        mapManager.loadMap(mapType);
        OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentTiledMap(), Map.UNIT_SCALE);

        player.update(mapManager, mapRenderer.getBatch(), 1.0f);

        verify(player).sendMessage(message, entityId);
    }

    private static Stream<Arguments> collisionMapData() {
        return Stream.of(
                Arguments.of(MapFactory.MapType.TOPPLE_ROAD_1, Component.MESSAGE.COLLISION_WITH_FOE, "RABITE"),
                Arguments.of(MapFactory.MapType.TOPPLE, Component.MESSAGE.COLLISION_WITH_ENTITY, "TOWN_INNKEEPER")
        );
    }
}
