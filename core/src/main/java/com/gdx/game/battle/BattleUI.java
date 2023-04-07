package com.gdx.game.battle;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.game.manager.ResourceManager;

public class BattleUI extends Window {

    private BattleState battleState;

    private Table topTable;
    private Table bottomTable;
    private Table attackTable;
    private BattleInventoryUI battleInventoryUI;

    public BattleUI(BattleState battleState, BattleInventoryUI battleInventoryUI) {
        super("battle", ResourceManager.skin);

        this.battleState = battleState;
        this.battleInventoryUI = battleInventoryUI;

        topTable = new Table();
        bottomTable = new Table();
        attackTable = new Table();

        handleAttackButton();
        handleObjectButton();
        handleEscapeButton();
        createAttackHUD();

        createBattleHUD();
    }

    private void handleAttackButton() {
        TextButton atkOptButton = new TextButton("Attack", ResourceManager.skin);
        topTable.add(atkOptButton).expand().fill();

        atkOptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleInventoryUI.setVisible(false);
                toAtkHUD();
            }
        });
    }

    private void handleObjectButton() {
        TextButton objOptButton = new TextButton("Object", ResourceManager.skin);
        topTable.add(objOptButton).expand().fill();

        objOptButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleInventoryUI.setVisible(!battleInventoryUI.isVisible());
            }
        });
    }

    private void handleEscapeButton() {
        TextButton escapeButton = new TextButton("Escape", ResourceManager.skin);
        bottomTable.add(escapeButton).expand().fill();

        escapeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleState.playerRuns();
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
