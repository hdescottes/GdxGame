package com.gdx.game.status;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.entities.player.characterClass.ClassObserver;
import com.gdx.game.entities.player.characterClass.ClassSubject;
import com.gdx.game.inventory.InventoryObserver;
import com.gdx.game.inventory.InventorySubject;
import com.gdx.game.manager.ResourceManager;
import com.gdx.game.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsUpUI extends Window implements ClassSubject, InventorySubject {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsUpUI.class);

    private TextButton validateBtn;
    private Array<InventoryObserver> observers;
    private Array<ClassObserver> classObservers;

    private final int maxPoint;
    private Label PValLabel;
    private int PVal;
    private int DPVal;
    private int bonusDPAdded = 0;
    private final int DPValInit;
    private int APVal;
    private int bonusAPAdded = 0;
    private final int APValInit;
    private int SPDPVal;
    private int bonusSPDPAdded = 0;
    private final int SPDPValInit;

    public StatsUpUI(int nbrLevelUp) {
        super("stats up", ResourceManager.skin);
        final int pointPerLvl = 5;
        this.maxPoint = nbrLevelUp * pointPerLvl;

        observers = new Array<>();
        classObservers = new Array<>();

        PVal = maxPoint;
        APValInit = ProfileManager.getInstance().getProperty("currentPlayerCharacterAP", Integer.class);
        DPValInit = ProfileManager.getInstance().getProperty("currentPlayerCharacterDP", Integer.class);
        SPDPValInit = ProfileManager.getInstance().getProperty("currentPlayerCharacterSPDP", Integer.class);
        APVal = APValInit;
        DPVal = DPValInit;
        SPDPVal = SPDPValInit;

        createPointsStatUp();
        this.row().padTop(30);
        createAtkStatUp();
        this.row().padTop(10);
        createDefStatUp();
        this.row().padTop(10);
        createSpdStatUp();
        this.row().padTop(30);
        validateBtn = createValidateButton();
    }

    private void createPointsStatUp() {
        Label bonusPLabel = new Label("Bonus Points : ", ResourceManager.skin);
        PValLabel = new Label(String.valueOf(PVal), ResourceManager.skin);

        this.add(bonusPLabel);
        this.add(PValLabel);
    }

    private void createAtkStatUp() {
        Label APLabel = new Label("Attack : ", ResourceManager.skin);
        Label APValLabel = new Label(String.valueOf(APVal), ResourceManager.skin);
        Label bonusAPAddedLabel = new Label(String.valueOf(bonusAPAdded), ResourceManager.skin);

        ImageButton btnPlus = new ImageButton(ResourceManager.skin, "plus");
        //btnPlus.setPosition(this.getWidth() * 5 / 6 - btnPlus.getWidth() / 2, this.getHeight());
        btnPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal > 0) {
                    APVal += 1;
                    APValLabel.setText(String.valueOf(APVal));
                    pValChange(false);
                    bonusAPAdded += 1;
                    bonusAPAddedLabel.setText(String.valueOf(bonusAPAdded));
                }
            }
        });

        ImageButton btnMinus = new ImageButton(ResourceManager.skin, "minus");
        //btnMinus.setPosition(this.getWidth() / 6 - btnMinus.getWidth() / 2, this.getHeight());
        btnMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal < maxPoint && (APVal > APValInit)) {
                    APVal -= 1;
                    APValLabel.setText(String.valueOf(APVal));
                    pValChange(true);
                    bonusAPAdded -= 1;
                    bonusAPAddedLabel.setText(String.valueOf(bonusAPAdded));
                }
            }
        });

        this.add(APLabel);
        this.row();
        this.add(btnMinus);
        this.add(APValLabel);
        this.add(btnPlus).padRight(10);
        this.add(createBonusPointAddedLabel());
        this.add(bonusAPAddedLabel);
    }

    private void createDefStatUp() {
        Label DPLabel = new Label("Defense: ", ResourceManager.skin);
        Label DPValLabel = new Label(String.valueOf(DPVal), ResourceManager.skin);
        Label bonusDPAddedLabel = new Label(String.valueOf(bonusDPAdded), ResourceManager.skin);

        ImageButton btnPlus = new ImageButton(ResourceManager.skin, "plus");
        //btnPlus.setPosition(DPValLabel.getX() * 5 / 6 - btnPlus.getWidth() / 2, DPValLabel.getY());
        btnPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal > 0) {
                    DPVal += 1;
                    DPValLabel.setText(String.valueOf(DPVal));
                    pValChange(false);
                    bonusDPAdded += 1;
                    bonusDPAddedLabel.setText(String.valueOf(bonusDPAdded));
                }
            }
        });

        ImageButton btnMinus = new ImageButton(ResourceManager.skin, "minus");
        //btnMinus.setPosition(DPValLabel.getX() / 6 - btnMinus.getWidth() / 2, DPValLabel.getY());
        btnMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal < maxPoint && (DPVal > DPValInit)) {
                    DPVal -= 1;
                    DPValLabel.setText(String.valueOf(DPVal));
                    pValChange(true);
                    bonusDPAdded -= 1;
                    bonusDPAddedLabel.setText(String.valueOf(bonusDPAdded));
                }
            }
        });

        this.add(DPLabel);
        this.row();
        this.add(btnMinus);
        this.add(DPValLabel);
        this.add(btnPlus).padRight(10);
        this.add(createBonusPointAddedLabel());
        this.add(bonusDPAddedLabel);
    }

    private void createSpdStatUp() {
        Label SPDPLabel = new Label("Speed: ", ResourceManager.skin);
        Label SPDPValLabel = new Label(String.valueOf(SPDPVal), ResourceManager.skin);
        Label bonusSPDPAddedLabel = new Label(String.valueOf(bonusSPDPAdded), ResourceManager.skin);

        ImageButton btnPlus = new ImageButton(ResourceManager.skin, "plus");
        //btnPlus.setPosition(SPDPValLabel.getX() * 5 / 6 - btnPlus.getWidth() / 2, SPDPValLabel.getY());
        btnPlus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal > 0) {
                    SPDPVal += 1;
                    SPDPValLabel.setText(String.valueOf(SPDPVal));
                    pValChange(false);
                    bonusSPDPAdded += 1;
                    bonusSPDPAddedLabel.setText(String.valueOf(bonusSPDPAdded));
                }
            }
        });

        ImageButton btnMinus = new ImageButton(ResourceManager.skin, "minus");
        //btnMinus.setPosition(SPDPValLabel.getX() / 6 - btnMinus.getWidth() / 2, SPDPValLabel.getY());
        btnMinus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PVal < maxPoint && (SPDPVal > SPDPValInit)) {
                    SPDPVal -= 1;
                    SPDPValLabel.setText(String.valueOf(SPDPVal));
                    pValChange(true);
                    bonusSPDPAdded -= 1;
                    bonusSPDPAddedLabel.setText(String.valueOf(bonusSPDPAdded));
                }
            }
        });

        this.add(SPDPLabel);
        this.row();
        this.add(btnMinus);
        this.add(SPDPValLabel);
        this.add(btnPlus).padRight(10);
        this.add(createBonusPointAddedLabel());
        this.add(bonusSPDPAddedLabel);
    }

    private TextButton createValidateButton() {
        TextButton validateBtn = new TextButton("SAVE", ResourceManager.skin);
        validateBtn.setPosition((this.getWidth() - validateBtn.getWidth()) / 2, this.getHeight() / 6);
        validateBtn.setVisible(false);
        validateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProfileManager.getInstance().setProperty("currentPlayerCharacterAP", APValInit + bonusAPAdded);
                ProfileManager.getInstance().setProperty("currentPlayerCharacterDP", DPValInit + bonusDPAdded);
                ProfileManager.getInstance().setProperty("currentPlayerCharacterSPDP", SPDPValInit + bonusSPDPAdded);
                LOGGER.info("Attack bonus point : {}", bonusAPAdded);
                LOGGER.info("Defense bonus point : {}", bonusDPAdded);
                LOGGER.info("Speed bonus point : {}", bonusSPDPAdded);
                setVisible(false);

                StatsUpUI.this.notify("", ClassObserver.ClassEvent.CHECK_UPGRADE_TREE_CLASS);
                StatsUpUI.this.notify("", InventoryObserver.InventoryEvent.REFRESH_STATS);
                remove();
            }
        });

        this.add(validateBtn);
        return validateBtn;
    }

    private Label createBonusPointAddedLabel() {
        return new Label("+", ResourceManager.skin);
    }

    private void pValChange(boolean inc) {
        if (inc) {
            PVal += 1;
        } else {
            PVal -= 1;
        }
        PValLabel.setText(String.valueOf(PVal));
        validateBtn.setVisible(PVal == 0);
    }

    @Override
    public void addObserver(InventoryObserver inventoryObserver) {
        observers.add(inventoryObserver);
    }

    @Override
    public void removeObserver(InventoryObserver inventoryObserver) {
        observers.removeValue(inventoryObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(InventoryObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String value, InventoryObserver.InventoryEvent event) {
        for(InventoryObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }

    @Override
    public void addObserver(ClassObserver classObserver) {
        classObservers.add(classObserver);
    }

    @Override
    public void removeObserver(ClassObserver classObserver) {
        classObservers.removeValue(classObserver, true);
    }

    @Override
    public void notify(String value, ClassObserver.ClassEvent event) {
        for(ClassObserver observer: classObservers) {
            observer.onNotify(value, event);
        }
    }
}
