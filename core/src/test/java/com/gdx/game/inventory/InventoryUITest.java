package com.gdx.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.manager.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Disabled
@ExtendWith(GdxRunner.class)
public class InventoryUITest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
        new ResourceManager();
    }

    @Test
    public void testInventoryUI_ShouldSucceed() {
        Entity entity = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR);
        InventoryUI inventoryUI = new InventoryUI(entity);

        assertThat(inventoryUI).isNotNull();
        assertThat(inventoryUI.getDragAndDrop()).isNotNull();
    }
}
