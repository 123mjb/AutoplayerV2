package com.chiefminingdad.autoplayer;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

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
