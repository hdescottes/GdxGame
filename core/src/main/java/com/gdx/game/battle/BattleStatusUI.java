package com.gdx.game.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.profile.ProfileManager;
import com.gdx.game.status.LevelTable;
import com.gdx.game.status.StatusObserver;
import com.gdx.game.status.StatusSubject;

import static com.gdx.game.manager.ResourceManager.STATUS_UI_SKIN;
import static com.gdx.game.manager.ResourceManager.STATUS_UI_TEXTURE_ATLAS;

public class BattleStatusUI extends Window implements StatusSubject {

    private ProfileManager profileManager = ProfileManager.getInstance();

    private Image hpBar;
    private Image mpBar;
    private Image xpBar;

    private Array<LevelTable> levelTables;
    private Array<StatusObserver> observers;
    private static final String LEVEL_TABLE_CONFIG = "scripts/level_tables.json";

    //Attributes
    int xpCurrentMax = profileManager.getProperty("currentPlayerXPMax", Integer.class);
    int xpVal = profileManager.getProperty("currentPlayerXP", Integer.class);

    int hpCurrentMax = profileManager.getProperty("currentPlayerHPMax", Integer.class);
    int hpVal = profileManager.getProperty("currentPlayerHP", Integer.class);

    int mpCurrentMax = profileManager.getProperty("currentPlayerMPMax", Integer.class);
    int mpVal = profileManager.getProperty("currentPlayerMP", Integer.class);

    int levelVal = profileManager.getProperty("currentPlayerLevel", Integer.class);

    private Label hpValLabel;
    private Label hpValLabelMax;
    private Label mpValLabel;
    private Label mpValLabelMax;
    private Label xpValLabel;
    private Label levelValLabel;

    private float barWidth = 0;
    private float barHeight = 0;

    public BattleStatusUI() {
        super("", STATUS_UI_SKIN);

        levelTables = LevelTable.getLevelTables(LEVEL_TABLE_CONFIG);

        observers = new Array<>();

        //Add to layout
        defaults().expand().fill();

        this.add();
        this.row();
        handleLevelLabel();
        handleHpBar();
        handleMpBar();
        handleXpBar();

        barWidth = hpBar.getWidth();
        barHeight = hpBar.getHeight();

        //set the current max values first
        this.setXPValueMax(xpCurrentMax);
        this.setHPValueMax(hpCurrentMax);
        this.setMPValueMax(mpCurrentMax);

        this.setXPValue(xpVal);
        this.setHPValue(hpVal);
        this.setMPValue(mpVal);

        this.setLevelValue(levelVal);

        //this.debug();
        this.pack();
    }

    private void handleLevelLabel() {
        Label levelLabel = new Label(" lv: ", STATUS_UI_SKIN);
        levelValLabel = new Label(String.valueOf(levelVal), STATUS_UI_SKIN);

        this.add(levelLabel).align(Align.left);
        this.add(levelValLabel).align(Align.left);
        this.row();
    }

    private void handleHpBar() {
        WidgetGroup group = new WidgetGroup();

        hpBar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("HP_Bar"));
        Image bar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"));

        Label hpLabel = new Label(" hp: ", STATUS_UI_SKIN);
        hpValLabel = new Label(String.valueOf(hpVal), STATUS_UI_SKIN);
        Label separator = new Label("/", STATUS_UI_SKIN);
        hpValLabelMax = new Label(String.valueOf(hpCurrentMax), STATUS_UI_SKIN);

        hpBar.setPosition(3, 6);

        group.addActor(bar);
        group.addActor(hpBar);

        this.add(group).size(bar.getWidth(), bar.getHeight()).padRight(10);
        this.add(hpLabel);
        this.add(hpValLabel);
        this.add(separator);
        this.add(hpValLabelMax);
        this.row();
    }

    private void handleMpBar() {
        WidgetGroup group2 = new WidgetGroup();

        mpBar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("MP_Bar"));
        Image bar2 = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"));

        Label mpLabel = new Label(" mp: ", STATUS_UI_SKIN);
        mpValLabel = new Label(String.valueOf(mpVal), STATUS_UI_SKIN);
        Label separator = new Label("/", STATUS_UI_SKIN);
        mpValLabelMax = new Label(String.valueOf(mpCurrentMax), STATUS_UI_SKIN);

        mpBar.setPosition(3, 6);

        group2.addActor(bar2);
        group2.addActor(mpBar);

        this.add(group2).size(bar2.getWidth(), bar2.getHeight()).padRight(10);
        this.add(mpLabel);
        this.add(mpValLabel);
        this.add(separator);
        this.add(mpValLabelMax);
        this.row();
    }

    private void handleXpBar() {
        WidgetGroup group3 = new WidgetGroup();

        xpBar = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("XP_Bar"));
        Image bar3 = new Image(STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"));

        Label xpLabel = new Label(" xp: ", STATUS_UI_SKIN);
        xpValLabel = new Label(String.valueOf(xpVal), STATUS_UI_SKIN);

        xpBar.setPosition(3, 6);

        group3.addActor(bar3);
        group3.addActor(xpBar);

        this.add(group3).size(bar3.getWidth(), bar3.getHeight()).padRight(10);
        this.add(xpLabel);
        this.add(xpValLabel).align(Align.left).padRight(20);
    }

    public int getLevelValue() {
        return levelVal;
    }

    public void setLevelValue(int levelValue) {
        this.levelVal = levelValue;
        levelValLabel.setText(String.valueOf(levelVal));
        notify(levelVal, StatusObserver.StatusEvent.UPDATED_LEVEL);
    }

    public int getXPValue() {
        return xpVal;
    }

    public void addXPValue(int xpValue) {
        this.xpVal += xpValue;

        if(xpVal > xpCurrentMax) {
            updateToNewLevel();
        }

        xpValLabel.setText(String.valueOf(xpVal));

        updateBar(xpBar, xpVal, xpCurrentMax);

        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValue(int xpValue) {
        this.xpVal = xpValue;

        if(xpVal > xpCurrentMax) {
            updateToNewLevel();
        }

        xpValLabel.setText(String.valueOf(xpVal));

        updateBar(xpBar, xpVal, xpCurrentMax);

        notify(xpVal, StatusObserver.StatusEvent.UPDATED_XP);
    }

    public void setXPValueMax(int maxXPValue) {
        this.xpCurrentMax = maxXPValue;
    }

    public void setStatusForLevel(int level) {
        for(LevelTable table: levelTables) {
            if(Integer.parseInt(table.getLevelID()) == level) {
                setXPValueMax(table.getXpMax());
                setXPValue(0);

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                return;
            }
        }
    }

    public void updateToNewLevel(){
        for(LevelTable table: levelTables) {
            //System.out.println("XPVAL " + _xpVal + " table XPMAX " + table.getXpMax() );
            if(xpVal <= table.getXpMax()) {
                setXPValueMax(table.getXpMax());

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                notify(levelVal, StatusObserver.StatusEvent.LEVELED_UP);
                return;
            }
        }
    }

    public int getXPValueMax() {
        return xpCurrentMax;
    }

    //HP
    public int getHPValue() {
        return hpVal;
    }

    public void removeHPValue(int hpValue) {
        hpVal = MathUtils.clamp(hpVal - hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void addHPValue(int hpValue) {
        hpVal = MathUtils.clamp(hpVal + hpValue, 0, hpCurrentMax);
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValue(int hpValue) {
        this.hpVal = hpValue;
        hpValLabel.setText(String.valueOf(hpVal));

        updateBar(hpBar, hpVal, hpCurrentMax);

        notify(hpVal, StatusObserver.StatusEvent.UPDATED_HP);
    }

    public void setHPValueMax(int maxHPValue) {
        this.hpCurrentMax = maxHPValue;
        hpValLabelMax.setText(String.valueOf(hpCurrentMax));
    }

    public int getHPValueMax() {
        return hpCurrentMax;
    }

    //MP
    public int getMPValue() {
        return mpVal;
    }

    public void removeMPValue(int mpValue) {
        mpVal = MathUtils.clamp(mpVal - mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void addMPValue(int mpValue) {
        mpVal = MathUtils.clamp(mpVal + mpValue, 0, mpCurrentMax);
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValue(int mpValue) {
        this.mpVal = mpValue;
        mpValLabel.setText(String.valueOf(mpVal));

        updateBar(mpBar, mpVal, mpCurrentMax);

        notify(mpVal, StatusObserver.StatusEvent.UPDATED_MP);
    }

    public void setMPValueMax(int maxMPValue) {
        this.mpCurrentMax = maxMPValue;
        mpValLabelMax.setText(String.valueOf(mpCurrentMax));
    }

    public int getMPValueMax() {
        return mpCurrentMax;
    }

    public void updateBar(Image bar, int currentVal, int maxVal) {
        int val = MathUtils.clamp(currentVal, 0, maxVal);
        float tempPercent = (float) val / (float) maxVal;
        float percentage = MathUtils.clamp(tempPercent, 0, 100);
        bar.setSize(barWidth *percentage, barHeight);
    }

    @Override
    public void addObserver(StatusObserver statusObserver) {
        observers.add(statusObserver);
    }

    @Override
    public void removeObserver(StatusObserver statusObserver) {
        observers.removeValue(statusObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(StatusObserver observer: observers) {
            observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(int value, StatusObserver.StatusEvent event) {
        for(StatusObserver observer: observers) {
            observer.onNotify(value, event);
        }
    }
}
