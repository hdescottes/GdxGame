package com.gdx.game.entities.player.characterClass.tree;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private int id;
    private String classId;
    private List<Node> classUpgrade;
    private ObjectMap<String, String> requirements;
    private ObjectMap<String, String> bonus;
    private Node left;
    private Node right;

    Node() {
        this.classUpgrade = new ArrayList<>();
        this.requirements = new ObjectMap<>();
        this.bonus = new ObjectMap<>();
    }

    public Node(int id, String classId, List<Node> classUpgrade, ObjectMap<String, String> requirements, ObjectMap<String, String> bonus) {
        this.id = id;
        this.classId = classId;
        this.classUpgrade = classUpgrade;
        this.requirements = requirements;
        this.bonus = bonus;
        this.left = null;
        this.right = null;
    }

    public int getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public List<Node> getClassUpgrade() {
        return classUpgrade;
    }

    public ObjectMap<String, String> getRequirements() {
        return requirements;
    }

    public ObjectMap<String, String> getBonus() {
        return bonus;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}
