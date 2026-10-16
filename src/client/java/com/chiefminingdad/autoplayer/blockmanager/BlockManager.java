package com.chiefminingdad.autoplayer.blockmanager;

import com.chiefminingdad.autoplayer.node.NodeManager;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * [BlockManager] handles all getting and storing of [BlockState] data to find the best path.
 */
public class BlockManager {
    NodeManager nodeManager;

    Dictionary<Vec3i, BlockState> blockStateDictionary = new Hashtable<>();

    public BlockManager(){

    }

    public void setNodeManager(NodeManager newNodeManager){
        nodeManager = newNodeManager;
    }

    public void reset(){
        blockStateDictionary = new Hashtable<>();
    }

}
