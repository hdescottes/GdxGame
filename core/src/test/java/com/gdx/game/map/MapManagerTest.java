package com.gdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.npc.NPCGraphicsComponent;
import com.gdx.game.entities.player.PlayerGraphicsComponent;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.profile.ProfileObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

@ExtendWith(GdxRunner.class)
public class MapManagerTest {

    private MockedConstruction<PlayerGraphicsComponent> mockPlayerGraphics;

    private MockedConstruction<NPCGraphicsComponent> mockNPCGraphics;

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        mockPlayerGraphics = mockConstruction(PlayerGraphicsComponent.class);
        mockNPCGraphics = mockConstruction(NPCGraphicsComponent.class);
    }

    @AfterEach
    void end() {
        mockPlayerGraphics.close();
        mockNPCGraphics.close();
    }

    @ParameterizedTest
    @MethodSource("currentMap")
    void testOnNotify(boolean isCurrentMap, MapFactory.MapType mapType) {
        ProfileManager profileManager = new ProfileManager();
        if (isCurrentMap) {
            profileManager.setProperty("currentMapType", mapType.name());
        }
        MapManager mapManager = new MapManager();

        mapManager.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        assertThat(mapManager.getCurrentMapType()).isEqualTo(mapType);
        assertThat(mapManager.hasMapChanged()).isTrue();
        assertThat(mapManager.getCurrentSelectedMapEntity()).isNull();
    }

    @Test
    public void testOnNotify_ShouldSucceedWithProfileLoadedAndPositionProperties() {
        ProfileManager profileManager = new ProfileManager();
        Vector2 toppleRoad1MapStartPosition = new Vector2(0, 0);
        Vector2 toppleMapStartPosition = new Vector2(1, 1);
        Vector2 currentPlayerPosition = new Vector2(2, 2);
        profileManager.setProperty("toppleRoad1MapStartPosition", toppleRoad1MapStartPosition);
        profileManager.setProperty("toppleMapStartPosition", toppleMapStartPosition);
        profileManager.setProperty("currentPlayerPosition", currentPlayerPosition);
        MapManager mapManager = new MapManager();

        mapManager.onNotify(profileManager, ProfileObserver.ProfileEvent.PROFILE_LOADED);

        assertThat(mapManager.getCurrentMapType()).isEqualTo(MapFactory.MapType.TOPPLE);
        assertThat(mapManager.hasMapChanged()).isTrue();
        assertThat(mapManager.getCurrentSelectedMapEntity()).isNull();
        assertThat(MapFactory.getMap(MapFactory.MapType.TOPPLE).getPlayerStart()).isEqualTo(new Vector2(currentPlayerPosition.x * 16, currentPlayerPosition.y * 16));
        assertThat(MapFactory.getMap(MapFactory.MapType.TOPPLE_ROAD_1).getPlayerStart()).isEqualTo(toppleRoad1MapStartPosition);
    }

    @Test
    public void testOnNotify_ShouldSucceedWithSavingProfile() {
        ProfileManager profileManager = new ProfileManager();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);

        mapManager.onNotify(profileManager, ProfileObserver.ProfileEvent.SAVING_PROFILE);

        HashMap<String, Vector2> profileProperties = new HashMap<>();
        profileProperties.put("currentPlayerPosition", player.getCurrentPosition());
        profileProperties.put("toppleMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE).getPlayerStart());
        profileProperties.put("toppleRoad1MapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE_ROAD_1).getPlayerStart());


        assertThat(profileManager).hasNoNullFieldsOrPropertiesExcept("currentMapType");
        assertThat(profileManager).hasFieldOrProperty("profileProperties");
        assertThat(profileManager.getProperty("currentPlayerPosition", Vector2.class)).isEqualTo(profileProperties.get("currentPlayerPosition"));
        assertThat(profileManager.getProperty("toppleMapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleMapStartPosition"));
        assertThat(profileManager.getProperty("toppleRoad1MapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleRoad1MapStartPosition"));
    }

    @Test
    public void testOnNotify_ShouldSucceedWithSavingProfileAndMapLoaded() {
        ProfileManager profileManager = new ProfileManager();
        Entity player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        MapManager mapManager = new MapManager();
        mapManager.setPlayer(player);

        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        mapManager.onNotify(profileManager, ProfileObserver.ProfileEvent.SAVING_PROFILE);

        HashMap<String, Object> profileProperties = new HashMap<>();
        profileProperties.put("currentMapType", MapFactory.MapType.TOPPLE.toString());
        profileProperties.put("currentPlayerPosition", player.getCurrentPosition());
        profileProperties.put("toppleMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE).getPlayerStart());
        profileProperties.put("toppleRoad1MapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE_ROAD_1).getPlayerStart());


        assertThat(profileManager).hasFieldOrProperty("profileProperties");
        assertThat(profileManager.getProperty("currentMapType", String.class)).isEqualTo(profileProperties.get("currentMapType"));
        assertThat(profileManager.getProperty("currentPlayerPosition", Vector2.class)).isEqualTo(profileProperties.get("currentPlayerPosition"));
        assertThat(profileManager.getProperty("toppleMapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleMapStartPosition"));
        assertThat(profileManager.getProperty("toppleRoad1MapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleRoad1MapStartPosition"));
    }

    @Test
    public void testOnNotify_ShouldSucceedWithClearCurrentProfile() {
        ProfileManager profileManager = new ProfileManager();
        MapManager mapManager = new MapManager();

        mapManager.loadMap(MapFactory.MapType.TOPPLE);
        mapManager.onNotify(profileManager, ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE);

        HashMap<String, Object> profileProperties = new HashMap<>();
        profileProperties.put("currentPlayerPosition", null);
        profileProperties.put("toppleMapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE).getPlayerStart());
        profileProperties.put("toppleRoad1MapStartPosition", MapFactory.getMap(MapFactory.MapType.TOPPLE_ROAD_1).getPlayerStart());


        assertThat(profileManager).hasNoNullFieldsOrPropertiesExcept("currentMapType");
        assertThat(profileManager).hasFieldOrProperty("profileProperties");
        assertThat(profileManager.getProperty("currentPlayerPosition", Vector2.class)).isEqualTo(profileProperties.get("currentPlayerPosition"));
        assertThat(profileManager.getProperty("toppleMapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleMapStartPosition"));
        assertThat(profileManager.getProperty("toppleRoad1MapStartPosition", Vector2.class)).isEqualTo(profileProperties.get("toppleRoad1MapStartPosition"));
    }

    @ParameterizedTest
    @MethodSource("currentMap")
    void testGetCurrentTiledMap(boolean isCurrentMap, MapFactory.MapType mapType) {
        MapManager mapManager = new MapManager();
        if (isCurrentMap) {
            mapManager.loadMap(mapType);
        }

        TiledMap tiledMap = mapManager.getCurrentTiledMap();

        assertThat(tiledMap).isEqualTo(MapFactory.getMap(mapType).getCurrentTiledMap());
    }

    private static Stream<Arguments> currentMap() {
        return Stream.of(
                Arguments.of(false, MapFactory.MapType.TOPPLE),
                Arguments.of(true, MapFactory.MapType.TOPPLE_ROAD_1)
        );
    }
}
