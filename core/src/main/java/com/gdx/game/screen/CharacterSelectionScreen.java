package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.CharacterRecord;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterSelectionScreen extends BaseScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSelectionScreen.class);

    private Stage characterSelectionStage = new Stage();
    private Table characterSelectionTable;
    private AnimatedImage playerImage;
    private TextButton startBtn;
    private TextButton nextBtn;
    private TextButton prevBtn;

    public HashMap<String, Entity> playerSprites;
    private Entity player;
    private int currentCharacter = 0;

    public CharacterSelectionScreen(GdxGame gdxGame, ResourceManager resourceManager) {
        super(gdxGame, resourceManager);

        characterSelectionTable = createTable();
        createCharacterMap();
        handleStartButton();
        handleNextButton();
        handlePreviousButton();
    }

    private void createCharacterMap() {
        playerSprites = new HashMap<>();
        playerSprites.put(CharacterRecord.CHAR_NAME_WARRIOR, EntityFactory.getInstance().getEntity(EntityFactory.EntityType.WARRIOR));
        playerSprites.put(CharacterRecord.CHAR_NAME_MAGE, EntityFactory.getInstance().getEntity(EntityFactory.EntityType.MAGE));
        playerSprites.put(CharacterRecord.CHAR_NAME_ROGUE, EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ROGUE));
        playerSprites.put(CharacterRecord.CHAR_NAME_GENERIC, EntityFactory.getInstance().getEntity(EntityFactory.EntityType.GENERIC));
        playerSprites.put(CharacterRecord.CHAR_NAME_ENGINEER, EntityFactory.getInstance().getEntity(EntityFactory.EntityType.ENGINEER));
    }

    private void handleCharacterImage() {
        player = playerSprites.get(CharacterRecord.CHARACTERS[currentCharacter].name);
        playerImage = new AnimatedImage();
        playerImage.setWidth(16);
        playerImage.setHeight(16);
        playerImage.setEntity(player);
        playerImage.setTouchable(Touchable.disabled);
        playerImage.setCurrentAnimation(Entity.AnimationType.IDLE);
        playerImage.setSize(playerImage.getWidth() * 3, playerImage.getHeight() * 3);
        playerImage.setPosition((characterSelectionStage.getWidth() - playerImage.getWidth()) / 2,
                (characterSelectionStage.getHeight() - playerImage.getHeight()) / 2);

        if(characterSelectionTable.getChildren().size == 4) {
            characterSelectionTable.removeActorAt(3, false);
        }
        characterSelectionTable.addActor(playerImage);
    }

    private void handleStartButton() {
        startBtn = new TextButton("START", ResourceManager.skin);
        startBtn.setPosition((characterSelectionStage.getWidth() - startBtn.getWidth()) / 2, characterSelectionStage.getHeight() / 6);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProfileManager.getInstance().setProperty("playerCharacter", EntityFactory.EntityType.valueOf(player.getEntityConfig().getEntityID()));

                gdxGame.setGameScreen(new GameScreen(gdxGame, resourceManager));
                LOGGER.info("Character " + playerImage.getEntity().getEntityConfig().getEntityID() + " selected");
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), gdxGame.getGameScreen(), new ArrayList<>());
            }
        });

        characterSelectionTable.addActor(startBtn);
    }

    private void handleNextButton() {
        nextBtn = new TextButton(">>>", ResourceManager.skin);
        nextBtn.setColor(Color.BLACK);
        nextBtn.setPosition(characterSelectionStage.getWidth() * 5 / 6 - nextBtn.getWidth() / 2, characterSelectionStage.getHeight() / 2);
        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentCharacter += 1;
                if(currentCharacter == CharacterRecord.CHARACTERS.length) {
                    currentCharacter = 0;
                }
            }
        });

        characterSelectionTable.addActor(nextBtn);
    }

    private void handlePreviousButton() {
        prevBtn = new TextButton("<<<", ResourceManager.skin);
        prevBtn.setColor(Color.BLACK);
        prevBtn.setPosition(characterSelectionStage.getWidth() / 6 - prevBtn.getWidth() / 2, characterSelectionStage.getHeight() / 2);
        prevBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentCharacter -= 1;
                if(currentCharacter < 0) {
                    currentCharacter = CharacterRecord.CHARACTERS.length - 1;
                }
            }
        });

        characterSelectionTable.addActor(prevBtn);
    }

    @Override
    public void show() {
        characterSelectionStage.addActor(characterSelectionTable);
        Gdx.input.setInputProcessor(characterSelectionStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        handleCharacterImage();

        gdxGame.getBatch().begin();
        //gdxGame.getBatch().draw();
        gdxGame.getBatch().end();

        characterSelectionStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        characterSelectionTable.remove();
    }
}
