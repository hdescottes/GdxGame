package com.gdx.game.entities.player.characterClass.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.gdx.game.entities.EntityConfig;
import com.gdx.game.profile.ProfileManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tree {

    private Node root;

    public Tree() {
    }

    public Node getRoot() {
        return root;
    }

    public static Tree buildClassTree(String configFilePath) {
        Json json = new Json();
        Node node = json.fromJson(Node.class, Gdx.files.internal(configFilePath));
        List<Node> nodeRoot = Collections.singletonList(node);
        List<Node> nodesInterClass = Stream.of(node)
                .flatMap(n -> n.getClassUpgrade().stream().distinct())
                .collect(Collectors.toList());
        List<Node> nodesFinalClass = Stream.of(node)
                .flatMap(n -> n.getClassUpgrade().stream().map(Node::getClassUpgrade))
                .flatMap(n2 -> n2.stream().distinct())
                .collect(Collectors.toList());
        List<Node> nodesTree = new ArrayList<>();
        Stream.of(nodeRoot, nodesInterClass, nodesFinalClass).forEach(nodesTree::addAll);

        Tree tree = new Tree();
        nodesTree.forEach(tree::add);

        return tree;
    }

    private void add(Node node) {
        root = addRecursive(root, node);
    }

    private Node addRecursive(Node current, Node newNode) {

        if (current == null) {
            return new Node(newNode.getId(), newNode.getClassId(), newNode.getClassUpgrade(), newNode.getRequirements(), newNode.getBonus());
        }

        if (newNode.getId() % 2 == 0) {
            if (newNode.getId() / 2 == current.getId()) {
                current.setLeft(addRecursive(current.getLeft(), newNode));
            } else if (newNode.getId() / 2 == current.getLeft().getId()){
                current.setLeft(addRecursive(current.getLeft(), newNode));
            } else {
                current.setRight(addRecursive(current.getRight(), newNode));
            }
        } else {
            if (newNode.getId() / 2 == current.getId()) {
                current.setRight(addRecursive(current.getRight(), newNode));
            } else if (newNode.getId() / 2 == current.getLeft().getId()) {
                current.setLeft(addRecursive(current.getLeft(), newNode));
            } else {
                current.setRight(addRecursive(current.getRight(), newNode));
            }
        }

        return current;
    }

    private List<Node> findToWhichClassesUpgrade(String currentClassId) {

        if (root == null) {
            return null;
        }

        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);

        while(!nodes.isEmpty()) {

            Node node = nodes.remove();

            if (node.getClassId().equals(currentClassId)) {
                return Arrays.asList(node.getLeft(), node.getRight());
            }

            if (node.getLeft() != null) {
                nodes.add(node.getLeft());
                if (node.getLeft().getClassId().equals(currentClassId)) {
                    return node.getLeft().getLeft() != null && node.getLeft().getRight() != null ?
                            Arrays.asList(node.getLeft().getLeft(), node.getLeft().getRight())
                            : Collections.emptyList();
                }
            }

            if (node.getRight() != null) {
                nodes.add(node.getRight());
                if (node.getRight().getClassId().equals(currentClassId)) {
                    return node.getRight().getLeft() != null && node.getRight().getRight() != null ?
                            Arrays.asList(node.getRight().getLeft(), node.getRight().getRight())
                            : Collections.emptyList();
                }
            }
        }
        return null;
    }

    public Node checkForClassUpgrade(String currentClassId, int currentPlayerCharacterAP, int currentPlayerCharacterDP) {
        List<Node> nodes = findToWhichClassesUpgrade(currentClassId);

        if (nodes != null && nodes.size() == 2) {
            int AP0 = Integer.parseInt(nodes.get(0).getRequirements().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name()));
            int DP0 = Integer.parseInt(nodes.get(0).getRequirements().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name()));
            int AP1 = Integer.parseInt(nodes.get(1).getRequirements().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_ATTACK_POINTS.name()));
            int DP1 = Integer.parseInt(nodes.get(1).getRequirements().get(EntityConfig.EntityProperties.ENTITY_PHYSICAL_DEFENSE_POINTS.name()));

            if (currentPlayerCharacterAP >= AP0 && currentPlayerCharacterDP >= DP0) {
                return nodes.get(0);
            } else if (currentPlayerCharacterAP >= AP1 && currentPlayerCharacterDP >= DP1) {
                return nodes.get(1);
            }
        }

        return null;
    }

    public static void saveNewClass(Node newClass) {
        if (newClass != null) {
            ProfileManager.getInstance().setProperty("characterClass", newClass.getClassId());
            ProfileManager.getInstance().setProperty("classBonus", newClass.getBonus());
        }
    }
}
