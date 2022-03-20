package com.gdx.game.entities.player.characterClass.tree;

import com.badlogic.gdx.utils.ObjectMap;
import com.gdx.game.GdxRunner;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(GdxRunner.class)
public class TreeTest {

    @Test
    public void testBuildClassTree_ShouldSucceed() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);

        assertThat(tree).isNotNull();
        assertThat(tree.getRoot()).isNotNull();
        assertThat(tree.getRoot().getClassId()).isEqualTo("WARRIOR");
        assertThat(tree.getRoot().getLeft()).isNotNull();
        assertThat(tree.getRoot().getLeft().getClassId()).isEqualTo("KNIGHT");
        assertThat(tree.getRoot().getRight()).isNotNull();
        assertThat(tree.getRoot().getRight().getClassId()).isEqualTo("GLADIATOR");
        assertThat(tree.getRoot().getLeft().getLeft()).isNotNull();
        assertThat(tree.getRoot().getLeft().getLeft().getClassId()).isEqualTo("PALADIN");
        assertThat(tree.getRoot().getLeft().getRight()).isNotNull();
        assertThat(tree.getRoot().getLeft().getRight().getClassId()).isEqualTo("WARLORD");
        assertThat(tree.getRoot().getRight().getLeft()).isNotNull();
        assertThat(tree.getRoot().getRight().getLeft().getClassId()).isEqualTo("DUELIST");
        assertThat(tree.getRoot().getRight().getRight()).isNotNull();
        assertThat(tree.getRoot().getRight().getRight().getClassId()).isEqualTo("WEAPON_MASTER");
    }

    @Test
    public void testCheckForClassUpgrade_ShouldSucceedWithInitialClass() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);

        Node node = tree.checkForClassUpgrade("WARRIOR", 20, 15);

        assertThat(node).isNotNull();
        assertThat(node.getClassId()).isEqualTo("KNIGHT");
    }

    @Test
    public void testCheckForClassUpgrade_ShouldSucceedWithInterClass() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);

        Node node = tree.checkForClassUpgrade("GLADIATOR", 40, 30);

        assertThat(node).isNotNull();
        assertThat(node.getClassId()).isEqualTo("WEAPON_MASTER");
    }

    @Test
    public void testCheckForClassUpgrade_ShouldSucceedWithInterClassBis() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);

        Node node = tree.checkForClassUpgrade("KNIGHT", 35, 35);

        assertThat(node).isNotNull();
        assertThat(node.getClassId()).isEqualTo("PALADIN");
    }

    @Test
    public void testCheckForClassUpgrade_ShouldSucceedWithFinalClass() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);

        Node node = tree.checkForClassUpgrade("WARLORD", 60, 60);

        assertThat(node).isNull();
    }

    @Test
    public void testSaveNewClass_ShouldSucceed() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);
        Node node = tree.checkForClassUpgrade("KNIGHT", 35, 35);

        Tree.saveNewClass(node);

        assertThat(ProfileManager.getInstance().getProperty("characterClass", String.class)).isEqualTo("PALADIN");
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class)).isNotNull();
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class).size).isEqualTo(2);
    }

    @Test
    public void testSaveNewClass_ShouldSucceedWithNoNewClass() {
        String configFilePath = "classes/tree_warrior.json";
        Tree tree = Tree.buildClassTree(configFilePath);
        Node node = tree.checkForClassUpgrade("KNIGHT", 35, 35);
        Tree.saveNewClass(node);

        Node node2 = tree.checkForClassUpgrade("PALADIN", 60, 60);
        Tree.saveNewClass(node2);

        assertThat(ProfileManager.getInstance().getProperty("characterClass", String.class)).isEqualTo("PALADIN");
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class)).isNotNull();
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class).size).isEqualTo(2);
    }
}
