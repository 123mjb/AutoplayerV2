package com.chiefminingdad.autoplayer.node;

import net.minecraft.core.Vec3i;

public class Node {
    public Vec3i location;
    public Vec3i previousLocation;

    public double weight = -1;

    public Node(Vec3i location, Vec3i previousLocation){
        this.location = location;
        this.previousLocation = previousLocation;
    }
    public Node(Vec3i location){
        this.location = location;
    }


}
