package com.gdx.game.screen.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.map.Map;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.BaseScreen;
import com.gdx.game.screen.GameScreen;
import com.gdx.game.screen.transition.effects.FadeOutTransitionEffect;
import com.gdx.game.screen.transition.effects.TransitionEffect;

import java.util.ArrayList;

public abstract class CutSceneBaseScreen extends GameScreen {
    private Stage stage;
    private Viewport viewport;
    private Stage UIStage;
    private Viewport UIViewport;
    private Actor followingActor;
    private Dialog messageBoxUI;
    private Label label;
    private boolean isCameraFixed = true;
    private Action switchScreenAction;

    public CutSceneBaseScreen(GdxGame game, ResourceManager resourceManager) {
        super(game, resourceManager);

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        UIViewport = new ScreenViewport(hudCamera);
        UIStage = new Stage(UIViewport);

        label = new Label("", ResourceManager.skin);
        label.setWrap(true);

        messageBoxUI = new Dialog("", ResourceManager.skin);
        messageBoxUI.setVisible(false);
        messageBoxUI.getContentTable().add(label).width(stage.getWidth()/2).pad(10, 10, 10, 0);
        messageBoxUI.pack();
        messageBoxUI.setPosition(stage.getWidth() / 2 - messageBoxUI.getWidth() / 2, stage.getHeight() - messageBoxUI.getHeight());

        followingActor = new Actor();
        followingActor.setPosition(0, 0);

        //Actions
        switchScreenAction = new RunnableAction(){
            @Override
            public void run() {
                ArrayList<TransitionEffect> effects = new ArrayList<>();
                effects.add(new FadeOutTransitionEffect(1f));
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), effects);
            }
        };

        UIStage.addActor(messageBoxUI);
    }

    abstract Action getCutsceneAction();

    AnimatedImage getAnimatedImage(EntityFactory.EntityType entityType){
        Entity entity = EntityFactory.getInstance().getEntity(entityType);
        return setEntityAnimation(entity);
    }

    private AnimatedImage setEntityAnimation(Entity entity){
        final AnimatedImage animEntity = new AnimatedImage();
        animEntity.setEntity(entity);
        animEntity.setWidth(17);
        animEntity.setHeight(17);
        animEntity.setSize(animEntity.getWidth() * Map.UNIT_SCALE, animEntity.getHeight() * Map.UNIT_SCALE);
        return animEntity;
    }

    void setCameraPosition(float x, float y){
        camera.position.set(x, y, 0f);
        isCameraFixed = true;
    }

    void showMessage(AnimatedImage animatedImage, String message){
        label.setText(message);
        messageBoxUI.getTitleLabel().setText(animatedImage.getName());
        messageBoxUI.pack();
        messageBoxUI.setVisible(true);
    }

    void hideMessage(){
        messageBoxUI.setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        mapRenderer.getBatch().enableBlending();
        mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (mapManager.hasMapChanged()) {
            mapRenderer.setMap(mapManager.getCurrentTiledMap());
            mapManager.setMapChanged(false);
        }

        mapRenderer.render();

        if (!isCameraFixed) {
            camera.position.set(followingActor.getX(), followingActor.getY(), 0f);
        }
        camera.update();

        UIStage.act(delta);
        UIStage.draw();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        stage.addAction(getCutsceneAction());
        ProfileManager.getInstance().removeAllObservers();
        if (mapRenderer == null) {
            mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentTiledMap(), Map.UNIT_SCALE);
        }
    }

    @Override
    public void hide() {
        ProfileManager.getInstance().removeAllObservers();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public Action getSwitchScreenAction() {
        return switchScreenAction;
    }
}
