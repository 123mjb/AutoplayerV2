package com.chiefminingdad.autoplayer.node;

import com.chiefminingdad.autoplayer.AutoPlayer;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Stores Information about "{@link Node}s" accessible by other sections of the mod.
 */
public class NodeManager {
    public Hashtable<Vec3i, Integer> ReferenceTable = new Hashtable<>();

    /**
     * The List containing all the Nodes which store their relation to each-other.
     */
    public ArrayList<Node> NodeList = new ArrayList<>();

    public NodeManager(Node first) {
        NodeList.add(first);
        ReferenceTable.put(first.location, 0);
    }

    /**
     * Used when the pathfinding needs to start from a new location as all previous data is useless.
     *
     * @param first Which node is going to be the starting node.
     */
    public void resetStartNode(Node first) {
        ReferenceTable = new Hashtable<>();
        NodeList = new ArrayList<>();
        NodeList.add(first);
        ReferenceTable.put(first.location, 0);
    }

    /**
     * Adds a new node to the system in the next available slot.
     *
     * @param newNode The node to be added
     */
    public void addNode(@NonNull Node newNode) {
        ReferenceTable.put(newNode.location, NodeList.size());
        NodeList.add(newNode);
    }

    /**
     * <i>Used in case a node is removed from the list and the dictionary needs to be updated.</i>
     */
    public void updateIndexes() {
        ReferenceTable.keys().asIterator().forEachRemaining(refer -> ReferenceTable.remove(refer));
        for (int i = 0; i < NodeList.size(); i++) {
            Vec3i loc = NodeList.get(i).location;
            ReferenceTable.put(loc, i);
        }
    }

    /**
     *
     * @param vector The position of the requested Node in the world.
     * @return The index of the node with the specified coordinate in the list.
     */
    public int getNodeIndex(Vec3i vector) {
        return ReferenceTable.getOrDefault(vector, -1);
    }

    public Node getNode(Vec3i vector){
        return NodeList.get(getNodeIndex(vector));
    }

    public Node getNode(int index){
        return NodeList.get(index);
    }

    public void setNode(Vec3i vector, Node newNode){
        NodeList.set(getNodeIndex(vector), newNode);
    }

    public boolean containsVector(Vec3i vector) {
        return ReferenceTable.getOrDefault(vector, -1) > 0;
    }

    /**
     * A function that will be used for the heuristic required for the A* pathfinding algorithm.
     *
     * @return The distance weight from the given node to the given end node.
     */
    public static double getDistanceWeight(@NonNull Vec3i CurrentNode, Vec3i FinalNode) {
        double manhattanWeight = CurrentNode.distManhattan(FinalNode);
        double sqrWeight = CurrentNode.distSqr(FinalNode);
//        return Math.sqrt((Math.pow(manhattanWeight, 2) + Math.pow(sqrWeight, 2)) / 2);
        return sqrWeight;
    }

    /**
     * Gets all surrounding nodes, adds all previously unused locations to the List and HashTable
     *
     * @param centre The position for all the new vectors to come from.
     * @return {@link Vec3i}[] The array of all locations that had a duplicate already stored.
     */
    public Vec3i[] addSurroundingNodes(Node centre) {
        Vec3i[] surrounding = getSurrounding(centre.location);
        ArrayList<Vec3i> duplicates = new ArrayList<>();

        for (Vec3i vector : surrounding) {
            // AutoPlayer.LOGGER.atInfo().log(vector.toString());
            if (!containsVector(vector)){
                addNode(new Node(vector,centre.location,centre.layer+1));
            }
            else {
                duplicates.add(vector);
            }
        }

        return duplicates.toArray(new Vec3i[0]);
    }

    public static Vec3i[] getSurrounding(Vec3i centre) {
        Vec3i[] surrounding = new Vec3i[26];
        int loc = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (!(i == 0 && j == 0 && k == 0)) {
                        surrounding[loc] = centre.offset(i, j, k);
                        loc++;
                    }
                }
            }
        }
        return surrounding;
    }


}
