package com.gdx.game.screen.cutscene;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.MapFactory;

public class CreatorIntroScreen extends CutSceneBaseScreen {
    private Action setupScene01;
    private AnimatedImage creator;

    public CreatorIntroScreen(GdxGame game, ResourceManager resourceManager) {
        super(game, resourceManager);

        creator = getAnimatedImage(EntityFactory.EntityType.THIEF);
        creator.setName("Creator");

        setupScene01 = new RunnableAction() {
            @Override
            public void run() {
                hideMessage();
                mapManager.loadMap(MapFactory.MapType.TOPPLE);
                mapManager.disableCurrentMapMusic();
                setCameraPosition(17, 10);

                creator.setCurrentAnimation(Entity.AnimationType.WALK_UP);
                creator.setVisible(true);
                creator.setPosition(17, 0);
            }
        };

        getStage().addActor(creator);
    }

    Action getCutsceneAction() {
        setupScene01.reset();
        getSwitchScreenAction().reset();

        return Actions.sequence(
                Actions.addAction(setupScene01),
                Actions.delay(1),
                Actions.addAction(Actions.moveTo(17, 10, 5, Interpolation.linear), creator),
                Actions.delay(Float.parseFloat("2.5")),
                Actions.addAction(Actions.run(() -> creator.setCurrentAnimation(Entity.AnimationType.IMMOBILE))),
                Actions.run(() -> showMessage(creator, "Hello adventurer! Welcome to my game, or at least my prototype game!")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "Many thanks for your interest in my project, i hope you will like it.")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "Do not hesitate to contribute or suggest any idea that you might have. Help is always welcome!")),
                Actions.delay(5),
                Actions.run(() -> showMessage(creator, "Have fun :)")),
                Actions.delay(3),
                Actions.after(getSwitchScreenAction())
        );
    }
}
