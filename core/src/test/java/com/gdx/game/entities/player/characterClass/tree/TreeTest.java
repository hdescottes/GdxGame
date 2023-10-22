package com.gdx.game.entities.player.characterClass.tree;

import com.badlogic.gdx.utils.ObjectMap;
import com.gdx.game.GdxRunner;
import com.gdx.game.profile.ProfileManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("classCheck")
    public void testCheckForClassUpgrade(String configFilePath, String currentClass, int attribute1, int attribute2, String expectedClassId) {
        Tree tree = Tree.buildClassTree(configFilePath);

        Node node = tree.checkForClassUpgrade(currentClass, attribute1, attribute2);

        if (expectedClassId != null) {
            assertThat(node).isNotNull();
            assertThat(node.getClassId()).isEqualTo(expectedClassId);
        } else {
            assertThat(node).isNull();
        }
    }

    private static Stream<Arguments> classCheck() {
        return Stream.of(
                Arguments.of("classes/tree_warrior.json", "WARRIOR", 20, 15, "KNIGHT"),
                Arguments.of("classes/tree_warrior.json", "GLADIATOR", 40, 30, "WEAPON_MASTER"),
                Arguments.of("classes/tree_warrior.json", "KNIGHT", 35, 35, "PALADIN"),
                Arguments.of("classes/tree_warrior.json", "WARLORD", 60, 60, null)
        );
    }

    @ParameterizedTest
    @MethodSource("saveNewClass")
    public void testSaveNewClass(String configFilePath, String currentClass, int attribute1, int attribute2, String expectedClassId, int expectedBonusSize) {
        Tree tree = Tree.buildClassTree(configFilePath);
        Node node = tree.checkForClassUpgrade(currentClass, attribute1, attribute2);
        Tree.saveNewClass(node);

        assertThat(ProfileManager.getInstance().getProperty("characterClass", String.class)).isEqualTo(expectedClassId);
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class)).isNotNull();
        assertThat(ProfileManager.getInstance().getProperty("classBonus", ObjectMap.class).size).isEqualTo(expectedBonusSize);
    }

    static Stream<Arguments> saveNewClass() {
        return Stream.of(
                Arguments.of("classes/tree_warrior.json", "KNIGHT", 35, 35, "PALADIN", 2),
                Arguments.of("classes/tree_warrior.json", "PALADIN", 60, 60, "PALADIN", 2)
        );
    }
}
