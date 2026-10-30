package com.chiefminingdad.autoplayer.node;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/**
 * Stores important information about each Node in the algorithm.
 */
public class Node {
    public Vec3i location;
    public Vec3i previousLocation = null;
    public int layer = -1;
    public boolean searched = false;

    public double weight = -1;

    /**
     * Should not be used as does not have the important information for Nodes to be used.
     * @param location Location of the Node.
     * @param previousLocation Location of the Parent Node.
     */
    public Node(Vec3i location, Vec3i previousLocation){
        this.location = location;
        this.previousLocation = previousLocation;
    }

    /**
     * Basic constructor for Node. Includes all needed information for a basic Node.
     * @param location Location of the Node.
     * @param previousLocation Location of the Parent Node.
     * @param layer The "layer" of the search tree the Node is in. Should be 0 for this constructor.
     *              Used to limit search depth.
     */
    public Node(Vec3i location, Vec3i previousLocation, int layer){
        this.location = location;
        this.previousLocation = previousLocation;
        this.layer = layer;
    }

    /**
     * For when a temporary node with just a location is required.
     * @param location The location of the Node.
     */
    public Node(Vec3i location){
        this.location = location;
    }

    /**
     * Used for the starting Node as it does not have a previous Node.
     * @param location The location of the Node.
     * @param layer The "layer" of the search tree the Node is in. Should be 0 for this constructor.
     *              Used to limit search depth.
     */
    public Node(Vec3i location, int layer){
        this.location = location;
        this.layer = layer;
    }

    @Override
    public String toString(){
        return String.format("Node(Loc:%s,PrevLoc:%s,Weight:%f)",
                location+"",
                previousLocation==null?"Empty":previousLocation+"",
                weight);
    }

}
