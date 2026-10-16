package com.chiefminingdad.autoplayer.node;

import net.minecraft.core.Vec3i;

public class Node {
    public Vec3i Location;
    public Vec3i PreviousLocation;

    public Node(Vec3i location, int index , Vec3i previousLocation){
        Location = location;
        PreviousLocation = previousLocation;
    }
    public Node(Vec3i location,int index){
        Location = location;
    }
}
