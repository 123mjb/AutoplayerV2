package com.chiefminingdad.autoplayer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

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

    public void addNode(@NonNull Node newNode){
        ReferenceTable.put(newNode.Location,NodeList.size());
        NodeList.add(newNode);
    }

    public void updateIndexes(){
        for(int i =0; i<NodeList.size();i++){
            Vec3i loc = NodeList.get(i).Location;
            ReferenceTable.put(loc,i);
        }
    }

    public int getNodeIndex(Vec3 vector){
        return ReferenceTable.get(vector);
    }
}
