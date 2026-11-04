package com.chiefminingdad.autoplayer.blockmanager;

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

    Hashtable<Vec3i, BlockState> blockStateDictionary = new Hashtable<>();

    private Minecraft client;
    private ClientLevel clientLevel;
    private Player player;

    public Minecraft getClient(){
        if(client==null) client = Minecraft.getInstance();
        return client;
    }
    
    public ClientLevel getClientLevel(){
        if(clientLevel==null) clientLevel = getClient().level;
        return clientLevel;
    }
    
    public Player getPlayer(){
        if(player == null) player = getClient().player;
        return player;
    }

    public BlockManager(){

    }

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

    public void setBlockState(Vec3i vector){
        if(!blockStateDictionary.containsKey(vector)){
            blockStateDictionary.put(vector,getBlockState(vector));
        }
    }

    public BlockState getBlockState(Vec3i location){
        return getBlockState(location, getClientLevel());
    }

    public static BlockState getBlockState(Vec3i location, ClientLevel level) throws RuntimeException{
        if(level==null) throw new RuntimeException("Level not initialised.");

        return level.getBlockState(new BlockPos(location));
    }

    public void setNodeManager(NodeManager newNodeManager){
        nodeManager = newNodeManager;
    }

    public void reset(){
        blockStateDictionary = new Hashtable<>();
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
