package com.chiefminingdad.autoplayer.pathmanager;

import com.chiefminingdad.autoplayer.blockmanager.BlockManager;
import com.chiefminingdad.autoplayer.node.Node;
import com.chiefminingdad.autoplayer.node.NodeManager;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.Vec3i;

public class PathManager {
    public BlockManager blockManager = new BlockManager();
    public NodeManager nodeManager;
    public Vec3i endLocation;

    public PathManager(Vec3i startingLocation,Vec3i finalLocation){
        nodeManager = new NodeManager(new Node(startingLocation,0));
        blockManager.setNodeManager(nodeManager);
        endLocation = finalLocation;
    }

    public void updateStartLocation(Vec3i newStartingLocation){
        nodeManager.resetStartNode(new Node(newStartingLocation,0));
        blockManager.reset();
    }
}
