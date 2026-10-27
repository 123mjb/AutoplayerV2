package com.chiefminingdad.autoplayer.blockmanager;

import com.chiefminingdad.autoplayer.node.NodeManager;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.WorldData;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * [BlockManager] handles all getting and storing of [BlockState] data to find the best path.
 */
public class BlockManager{
    NodeManager nodeManager;

    Dictionary<Vec3i, BlockState> blockStateDictionary = new Hashtable<>();

    private Minecraft client;
    private ClientLevel clientLevel;
    private User user;

    public Minecraft getClient(){
        if(client==null) client = Minecraft.getInstance();
        return client;
    }
    
    public ClientLevel getClientLevel(){
        if(clientLevel==null) clientLevel = getClient().level;
        return clientLevel;
    }
    
    public User getUser(){
        if(user == null) user = getClient().getUser();
        return user;
    }

    public BlockManager(){

    }

    /**
     *
     * @return {@link Boolean} Whether there was a change in value of the block states.
     */
    public boolean getAllBlockStates(){
        boolean changed = false;
        nodeManager.ReferenceTable.keys().asIterator().forEachRemaining(vec3i -> {
            BlockState previousBlockState = blockStateDictionary.get(vec3i);
            if(previousBlockState==null) blockStateDictionary.put(vec3i, getBlockState(vec3i));
            else if (!previousBlockState.equals(getBlockState(vec3i))) {
                changed = true;
            }
        });

    }

    public BlockState getBlockState(Vec3i location) throws RuntimeException{
        if(getClientLevel()==null) throw new RuntimeException("Level not initialised.");

        return clientLevel.getBlockState(new BlockPos(location));
    }

    public void setNodeManager(NodeManager newNodeManager){
        nodeManager = newNodeManager;
    }

    public void reset(){
        blockStateDictionary = new Hashtable<>();
    }


}
