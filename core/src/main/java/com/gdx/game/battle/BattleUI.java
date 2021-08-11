package com.gdx.game.battle;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.manager.ResourceManager;

public class BattleUI extends Window {

    private BattleState battleState;

    private TextButton atkOptButton;
    private TextButton objOptButton;
    private TextButton endTurnButton;
    private Table topTable;
    private Table bottomTable;
    private Table attackTable;

    public BattleUI(BattleState battleState) {
        super("battle", ResourceManager.skin);

        this.battleState = battleState;

        topTable = new Table();
        bottomTable = new Table();
        attackTable = new Table();

        handleAttackButton();
        handleObjectButton();
        handleEndTurnButton();
        createAttackHUD();

        createBattleHUD();
    }

    private void handleAttackButton() {
        atkOptButton = new TextButton("Attack", ResourceManager.skin);
        topTable.add(atkOptButton).expand().fill();

        atkOptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toAtkHUD();
            }
        });
    }

    private void handleObjectButton() {
        objOptButton = new TextButton("Object", ResourceManager.skin);
        topTable.add(objOptButton).expand().fill();

        objOptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
    }

    private void handleEndTurnButton() {
        endTurnButton = new TextButton("End Turn", ResourceManager.skin);
        bottomTable.add(endTurnButton).expand().fill();

        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
    }

    private void createAttackHUD() {
        TextButton attackButton = new TextButton("Atk", ResourceManager.skin);
        attackTable.add(attackButton).expand().fill();

        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleState.playerAttacks();
            }
        });

        TextButton backButton = new TextButton("Back", ResourceManager.skin);
        attackTable.add(backButton).expand().fill();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toBattleHUD();
            }
        });
    }

    private void createBattleHUD() {
        this.defaults().expand().fill();
        toBattleHUD();
        this.pack();
    }

    private void toBattleHUD() {
        this.clearChildren();

        this.add(topTable);
        this.row();
        this.add(bottomTable);
    }

    private void toAtkHUD() {
        this.clearChildren();

        this.add(attackTable);
    }
}
