package com.chiefminingdad.autoplayer;

import net.minecraft.world.phys.Vec3;

public class Node {
    Vec3 Location;
    int Index;
    int PreviousIndex;

    public Node(Vec3 location,int index , int previousIndex){
        Location = location;
        Index = index;
        PreviousIndex = previousIndex;
    }
    public Node(Vec3 location,int index){
        Location = location;
        Index = index;
    }
}
