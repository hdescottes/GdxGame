package com.gdx.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.gdx.game.GdxRunner;
import com.gdx.game.entities.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Disabled
@ExtendWith(GdxRunner.class)
public class PlayerGraphicsComponentTest {

    @BeforeEach
    void init() {
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = mock(GL20.class);
    }

    @Test
    public void testReceivedMessage_ShouldSucceedWithLoadAnimation() {
        PlayerGraphicsComponent playerGraphicsComponent = new PlayerGraphicsComponent();
        String message = "LOAD_ANIMATIONS:::::{animationConfig:[{frameDuration:0.15,texturePaths:[sprites/characters/Warrior.png],gridPoints:[{},{y:1},{y:2},{y:3}]},{frameDuration:0.15,animationType:WALK_DOWN,texturePaths:[sprites/characters/Warrior.png],gridPoints:[{},{y:1},{y:2},{y:3}]},{frameDuration:0.15,animationType:WALK_LEFT,texturePaths:[sprites/characters/Warrior.png],gridPoints:[{x:1},{x:1,y:1},{x:1,y:2},{x:1,y:3}]},{frameDuration:0.15,animationType:WALK_RIGHT,texturePaths:[sprites/characters/Warrior.png],gridPoints:[{x:2},{x:2,y:1},{x:2,y:2},{x:2,y:3}]},{frameDuration:0.15,animationType:WALK_UP,texturePaths:[sprites/characters/Warrior.png],gridPoints:[{x:3},{x:3,y:1},{x:3,y:2},{x:3,y:3}]}],inventory:[ARMOR04,BOOTS03,HELMET05,POTIONS01,POTIONS01,POTIONS01,POTIONS01,SCROLL01,SCROLL01,SCROLL01,SHIELD02,WANDS02,WEAPON01],entityID:PLAYER,conversationConfigPath:\"\",questConfigPath:\"\",currentQuestID:\"\",itemTypeID:NONE}";
        playerGraphicsComponent.receiveMessage(message);

        assertThat(playerGraphicsComponent.getAnimation(Entity.AnimationType.WALK_DOWN).getAnimationDuration()).isEqualTo(0.6f);
        assertThat(playerGraphicsComponent.getAnimation(Entity.AnimationType.WALK_DOWN).getFrameDuration()).isEqualTo(0.15f);
        assertThat(playerGraphicsComponent.getAnimation(Entity.AnimationType.WALK_DOWN).getKeyFrames()).hasSize(4);
        assertThat(playerGraphicsComponent.getAnimation(Entity.AnimationType.WALK_DOWN).getPlayMode().name()).isEqualTo("LOOP");
    }

    @Test
    public void testReceivedMessage_ShouldSucceedWithInitStartPosition() {
        PlayerGraphicsComponent playerGraphicsComponent = new PlayerGraphicsComponent();
        String message = "INIT_START_POSITION:::::{x:16.291687,y:4.104187}";
        playerGraphicsComponent.receiveMessage(message);

        assertThat(playerGraphicsComponent.getCurrentPosition()).isEqualTo(new Vector2(16.291687f, 4.104187f));
    }

    @Test
    public void testReceivedMessage_ShouldSucceedWithCurrentPosition() {
        PlayerGraphicsComponent playerGraphicsComponent = new PlayerGraphicsComponent();
        String message = "CURRENT_POSITION:::::{x:16.291687,y:4.019291}";
        playerGraphicsComponent.receiveMessage(message);

        assertThat(playerGraphicsComponent.getCurrentPosition()).isEqualTo(new Vector2(16.291687f, 4.019291f));
    }
}
