package com.chiefminingdad.autoplayer;

import net.minecraft.world.phys.Vec3;

import java.util.Dictionary;
import java.util.Hashtable;

public class NodeManager {
    Dictionary<Vec3, Integer> ReferenceTable = new Hashtable<>();



    NodeManager(){

    }

    public int getNodeIndex(Vec3 vector){
        return ReferenceTable.get(vector);
    }
}
