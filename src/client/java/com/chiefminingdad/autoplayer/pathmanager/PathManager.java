package com.chiefminingdad.autoplayer.pathmanager;

import com.chiefminingdad.autoplayer.AutoPlayerClient;
import com.chiefminingdad.autoplayer.node.Node;
import com.chiefminingdad.autoplayer.node.NodeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;

/**
 * Manages the logic for all the pathfinding so that most of the logic is in one place and easier to read.
 */
public class PathManager {
    public NodeManager nodeManager;
    public Vec3i endLocation;

    private Minecraft client;
    private Player player;

    public Minecraft getClient(){
        if(client==null) client = Minecraft.getInstance();
        return client;
    }

    public Player getPlayer(){
        if(player == null) player = getClient().player;
        return player;
    }

    public PathManager(Vec3i startingLocation,Vec3i finalLocation){
        nodeManager = new NodeManager(new Node(startingLocation));
        AutoPlayerClient.blockManager.setNodeManager(nodeManager);
        endLocation = finalLocation;
    }

    public PathManager(Vec3i finalLocation){
        endLocation = finalLocation;
        nodeManager = new NodeManager(new Node(getPlayer().getOnPos().above()));
    }

    /**
     * Used when the circumstances the pathfinding is working with changes and pathfinding has to restart.
     * @param newStartingLocation the new location pathfinding will start from.
     */
    public void updateStartLocation(Vec3i newStartingLocation){
        nodeManager.resetStartNode(new Node(newStartingLocation));
        AutoPlayerClient.blockManager.reset();
    }

    // TODO: Add code to keep BlockManager up to date and run PathManager.run() from another thread so it doesnt impact performance.
    /**
     * Runs one round of expanding the path.
     */
    public void run(){
        setAllWeights();

        Vec3i nextNode = getBestNextNode();

        Vec3i[] duplicateNodes = nodeManager.addSurroundingNodes(nextNode);
        setBetterNodeWeights(duplicateNodes,nextNode);

    }

    /**
     * For all collisions when adding new nodes,
     * compares the values of the weights of the nodes in the collision and the keeps the lowest value.
     * @param newNodes The {@link Vec3i}s that encountered a collision.
     * @param previousNode The location of the previous Node the new ones were generated from.
     */
    private void setBetterNodeWeights(Vec3i[] newNodes, Vec3i previousNode){
        for(Vec3i vector: newNodes){
            Node storedNode = nodeManager.getNode(vector);
            Node newNode = new Node(vector,previousNode);
            double newNodeWeight = generateWeightValue(newNode);
            if(newNodeWeight < storedNode.weight){
                newNode.weight = newNodeWeight;
                nodeManager.setNode(vector,newNode);
            }
        }
    }

    private Vec3i getBestNextNode(){
        Node bestNode = new Node(new Vec3i(0,0,0));
        bestNode.weight = Double.MAX_VALUE;

        for (int i = 0; i < nodeManager.NodeList.toArray().length; i++) {
            Node current = nodeManager.getNode(i);
            if(current.weight< bestNode.weight){
                bestNode = current;
            }
        }
        return bestNode.location;
    }

    private void setAllWeights(){
        for (int i = 0; i < nodeManager.NodeList.toArray().length; i++) {
            if(nodeManager.getNode(i).weight<0) {
                setWeight(i);
            }
        }
    }

    private void setWeight(int index){
        Node node = nodeManager.NodeList.get(index);
        if(node.previousLocation ==null){
            node.weight = 0;
        }
        else {
            Node previousNode = nodeManager.getNode(node.previousLocation);
            node.weight = generateWeightValue(node, previousNode);
        }
    }

    /**
     * At the moment a primitive implementation to check that the rest of the systems work first.
     * @return The weight of the current Node
     */
    private double generateWeightValue(Node currentNode, Node previousNode){
        return currentNode.location.distSqr(previousNode.location) + previousNode.weight;
    }
    private double generateWeightValue(Node currentNode){
        Node previousNode = nodeManager.getNode(currentNode.previousLocation);
        return currentNode.location.distSqr(previousNode.location) + previousNode.weight;
    }


}
