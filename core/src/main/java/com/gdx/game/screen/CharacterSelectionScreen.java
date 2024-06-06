package com.gdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gdx.game.GdxGame;
import com.gdx.game.animation.AnimatedImage;
import com.gdx.game.entities.Entity;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.entities.EntityFactory;
import com.gdx.game.entities.player.CharacterRecord;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.screen.cutscene.CreatorIntroScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class CharacterSelectionScreen extends BaseScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSelectionScreen.class);

    private Stage characterSelectionStage = new Stage();
    private Table characterSelectionTable;
    private AnimatedImage playerImage;
    private Label characterClass;
    private Label atkStat;
    private Label defStat;
    private Label spdStat;
    private TextButton startBtn;
    private TextButton nextBtn;
    private TextButton prevBtn;

    public Map<String, Entity> playerSprites;
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
        playerSprites = CharacterRecord.charactersList.stream()
                .collect(Collectors.toMap(EntityFactory.EntityType::name, EntityFactory.getInstance()::getEntity));
    }

    private void handleCharacterImage() {
        player = playerSprites.get(CharacterRecord.CHARACTERS[currentCharacter].getName());
        playerImage = new AnimatedImage();
        playerImage.setWidth(16);
        playerImage.setHeight(16);
        playerImage.setEntity(player);
        playerImage.setTouchable(Touchable.disabled);
        playerImage.setCurrentAnimation(Entity.AnimationType.IDLE);
        playerImage.setSize(playerImage.getWidth() * 3, playerImage.getHeight() * 3);
        playerImage.setPosition((characterSelectionStage.getWidth() - playerImage.getWidth()) / 2,
                (characterSelectionStage.getHeight() - playerImage.getHeight()) / 2);

        if (characterSelectionTable.getChildren().size == 7) {
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
                int APVal = Integer.parseInt(player.getEntityConfig().getEntityProperties().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name()));
                int DPVal = Integer.parseInt(player.getEntityConfig().getEntityProperties().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name()));
                int SPDPVal = Integer.parseInt(player.getEntityConfig().getEntityProperties().get(EntityConfig.EntityProperties.ENTITY_SPEED_POINTS.name()));
                ProfileManager.getInstance().setProperty("playerCharacter", EntityFactory.EntityType.valueOf(player.getEntityConfig().getEntityID()));
                ProfileManager.getInstance().setProperty("characterClass", player.getEntityConfig().getEntityID());
                ProfileManager.getInstance().setProperty("currentPlayerAP", APVal);
                ProfileManager.getInstance().setProperty("currentPlayerDP", DPVal);
                ProfileManager.getInstance().setProperty("currentPlayerSPDP", SPDPVal);
                ProfileManager.getInstance().setProperty("currentPlayerCharacterAP", APVal);
                ProfileManager.getInstance().setProperty("currentPlayerCharacterDP", DPVal);
                ProfileManager.getInstance().setProperty("currentPlayerCharacterSPDP", SPDPVal);

                gdxGame.setGameScreen(new GameScreen(gdxGame, resourceManager));
                LOGGER.info("Character {} selected", playerImage.getEntity().getEntityConfig().getEntityID());
                setScreenWithTransition((BaseScreen) gdxGame.getScreen(), new CreatorIntroScreen(gdxGame, resourceManager), new ArrayList<>());
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
                if (currentCharacter == CharacterRecord.CHARACTERS.length) {
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
                if (currentCharacter < 0) {
                    currentCharacter = CharacterRecord.CHARACTERS.length - 1;
                }
            }
        });

        characterSelectionTable.addActor(prevBtn);
    }

    private void handleStatDisplay() {
        characterClass = prepareStatLabel(CharacterRecord.CHARACTERS[currentCharacter].getName(),
                3 * characterSelectionStage.getWidth() / 4, 5 * characterSelectionStage.getHeight() / 6,
                ResourceManager.skin);

        atkStat = prepareStatLabel("ATK: " + CharacterRecord.CHARACTERS[currentCharacter].getBaseAttack(),
                characterClass.getX(), 3 * characterSelectionStage.getHeight() / 4,
                ResourceManager.skin);

        defStat = prepareStatLabel("DEF: " + CharacterRecord.CHARACTERS[currentCharacter].getBaseDefense(),
                atkStat.getX(), atkStat.getY() - 30,
                ResourceManager.skin);

        spdStat = prepareStatLabel("SPD: " + CharacterRecord.CHARACTERS[currentCharacter].getBaseSpeed(),
                defStat.getX(), defStat.getY() - 30,
                ResourceManager.skin);

        if (characterSelectionTable.getChildren().size == 9) {
            characterSelectionTable.removeActorAt(3, false);
            characterSelectionTable.removeActorAt(3, false);
            characterSelectionTable.removeActorAt(3, false);
            characterSelectionTable.removeActorAt(3, false);
            characterSelectionTable.removeActorAt(3, false);
        }
        characterSelectionTable.addActor(characterClass);
        characterSelectionTable.addActor(atkStat);
        characterSelectionTable.addActor(defStat);
        characterSelectionTable.addActor(spdStat);
    }

    private Label prepareStatLabel(String text, float x, float y, Skin skin) {
        Label lbl = new Label(text, skin);
        lbl.setAlignment(Align.left);
        lbl.setPosition(x, y);
        return lbl;
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
        handleStatDisplay();

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
