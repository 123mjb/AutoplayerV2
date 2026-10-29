package com.chiefminingdad.autoplayer.node;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class Node {
    public Vec3i location;
    public Vec3i previousLocation = null;
    public int layer = -1;
    public boolean searched = false;

    public double weight = -1;

    public Node(Vec3i location, Vec3i previousLocation){
        this.location = location;
        this.previousLocation = previousLocation;
    }
    public Node(Vec3i location, Vec3i previousLocation, int layer){
        this.location = location;
        this.previousLocation = previousLocation;
        this.layer = layer;
    }
    public Node(Vec3i location){
        this.location = location;
    }

    public Node(Vec3i location, int layer){
        this.location = location;
        this.layer = layer;
    }

    @Override
    public String toString(){
        return String.format("Node(Loc:%s,PrevLoc:%s,Weight:%f)",location+"",previousLocation==null?"Empty":previousLocation+"",weight);
    }

}
