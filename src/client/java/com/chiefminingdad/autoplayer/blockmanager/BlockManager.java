package com.chiefminingdad.autoplayer.blockmanager;

import com.chiefminingdad.autoplayer.node.Node;
import com.chiefminingdad.autoplayer.node.NodeManager;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * {@link BlockManager} handles all getting and storing of {@link BlockState} data to find the best path.
 */
public class BlockManager{
    NodeManager nodeManager;

    private Hashtable<Vec3i, BlockState> blockStateDictionary = new Hashtable<>();

    private Minecraft client;
    private ClientLevel clientLevel;
    private Player player;

    private Minecraft getClient(){
        if(client==null) client = Minecraft.getInstance();
        return client;
    }
    
    private ClientLevel getClientLevel(){
        if(clientLevel==null) clientLevel = getClient().level;
        return clientLevel;
    }
    
    private Player getPlayer(){
        if(player == null) player = getClient().player;
        return player;
    }

    public BlockManager(){

    }

    /**
     * Gets the Vectors of all Nodes and the locations above and below, needed for pathfinding.
     * @return Array of all the vectors of the block states needed to be stored.
     */
    private Vec3i[] getVectors(){
        ArrayList<Vec3i> vectors = new ArrayList<>();

        for(Iterator<Vec3i> e = nodeManager.ReferenceTable.keys().asIterator(); e.hasNext();){
            Vec3i vector = e.next();
            for (int i = -1; i <=1 ; i++) {
                if(!vectors.contains(vector.offset(0,i,0))){
                    vectors.add(vector);
                }
            }
        }
        return vectors.toArray(new Vec3i[0]);
    }

    /**
     *
     * @return {@link Boolean} Whether there was a change in value of the block states.
     */
    public boolean setAllBlockStates(){
        boolean changed = false;
        for(Vec3i vector: getVectors()){
            if(blockStateDictionary.put(vector, getBlockState(vector))!=null){
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Gets the {@link BlockState} in a location and sets it in the HashTable
     * @param vector The location of the requested {@link BlockState}
     */
    public void setBlockState(Vec3i vector){
        if(!blockStateDictionary.containsKey(vector)){
            blockStateDictionary.put(vector,getBlockState(vector));
        }
    }

    /**
     * Gets a BlockState at a location.
     * @param location The location of the block requested.
     * @return The BlockState of the requested Block.
     */
    public BlockState getBlockState(Vec3i location){
        if(!blockStateDictionary.containsKey(location)) {
            return getBlockState(location, getClientLevel());
        }
        return blockStateDictionary.get(location);
    }

    /**
     * Gets the BlockState at a location in a level.
     * @param location The location of the block requested.
     * @param level The Level (World) Loaded
     * @return The {@link BlockState} of the requested Block.
     * @throws RuntimeException If this function is run while the Level has not been initialised.
     */
    public static BlockState getBlockState(Vec3i location, ClientLevel level) throws RuntimeException{
        if(level==null) throw new RuntimeException("Level not initialised.");

        return level.getBlockState(new BlockPos(location));
    }

    /**
     * Sets the nodeManager variable in this class for easier access in this class.
     * @param newNodeManager The Main used NodeManager.
     */
    public void setNodeManager(NodeManager newNodeManager){
        nodeManager = newNodeManager;
    }

    /**
     * Resets the HashTable back to empty.
     */
    public void reset(){
        blockStateDictionary = new Hashtable<>();
    }

    public float getMovementWeight(Node node){
        Vec3i previous = node.previousLocation;
        Vec3i current = node.location;
        float start = getMovementWeight(previous);
        float end = getMovementWeight(current);
        float distXY = (float) Math.sqrt(
                Math.pow(previous.getX()-current.getX(),2) +
                        Math.pow(previous.getY()-current.getY(),2));
        return start * distXY / 2 + end * distXY / 2;
    }
    private float getMovementWeight(Vec3i loc){
        BlockState head = getBlockState(loc.above());
        BlockState feet = getBlockState(loc);
        BlockState standing = getBlockState(loc.below());
        return 0; // TODO: Add the Logic to work out the movement weights of a specific block.
    }




    public static class Tests{
        public static int getBlock(CommandContext<FabricClientCommandSource> commandContext){
            try {
                Minecraft client = Minecraft.getInstance();
                ClientLevel level = client.level;
                Player player = client.player;

                if (player==null) throw new RuntimeException("Player Is Null, Is this being run outside a world?");
                BlockState blockState = getBlockState(player.getOnPos(),level);

                commandContext.getSource().sendFeedback(Component.literal(blockState.toString()));
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }
    }

}
