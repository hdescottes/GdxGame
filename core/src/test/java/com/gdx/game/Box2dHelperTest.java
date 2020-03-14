package com.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.gdx.game.box2d.Box2dHelper;
import com.gdx.game.box2d.Box2dWorld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(GdxRunnerTest.class)
public class Box2dHelperTest {

    @BeforeEach
    void init() {
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    void testCreateBody() {
        Box2dWorld box2d = new Box2dWorld();
        float width = 10;
        float height = 10;
        int xOffset = 1;
        int yOffset = 1;
        Vector3 pos3 = new Vector3(1,1,1);

        Body body = Box2dHelper.createBody(box2d.getWorld(), width, height, xOffset, yOffset, pos3, null, BodyDef.BodyType.DynamicBody);

        assertThat(body.getPosition()).isEqualTo(new Vector2(pos3.x + width/2 + xOffset, pos3.y + height/2 + yOffset));
        assertThat(body.getFixtureList().size).isEqualTo(1);
        assertThat(body.getFixtureList().first().getRestitution()).isEqualTo(0.4f);
    }
}
