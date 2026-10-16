package com.chiefminingdad.autoplayer.node;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class NodeManager {
    Dictionary<Vec3i, Integer> ReferenceTable = new Hashtable<>();

    ArrayList<Node> NodeList = new ArrayList<>();

    public NodeManager(Node first){
        NodeList.add(first);
        ReferenceTable.put(first.Location,0);
    }

    public void resetStartNode(Node first){
        ReferenceTable = new Hashtable<>();
        NodeList = new ArrayList<>();
        NodeList.add(first);
        ReferenceTable.put(first.Location,0);
    }

    public void addNode(@NonNull Node newNode){
        ReferenceTable.put(newNode.Location,NodeList.size());
        NodeList.add(newNode);
    }

    public void updateIndexes(){
        ReferenceTable.keys().asIterator().forEachRemaining(refer->ReferenceTable.remove(refer));
        for(int i =0; i<NodeList.size();i++){
            Vec3i loc = NodeList.get(i).Location;
            ReferenceTable.put(loc,i);
        }
    }

    public int getNodeIndex(Vec3 vector){
        return ReferenceTable.get(vector);
    }

    /**
     *
     * @return The distance weight from the given node to the given end node.
     */
    double getDistanceWeight(Vec3i CurrentNode, Vec3i FinalNode){
        double manhattanWeight = CurrentNode.distManhattan(FinalNode);
        double sqrWeight = CurrentNode.distSqr(FinalNode);
        return Math.sqrt((Math.pow(manhattanWeight,2)+Math.pow(sqrWeight,2))/2);
    }


}
