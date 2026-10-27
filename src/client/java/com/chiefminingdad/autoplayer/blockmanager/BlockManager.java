package com.chiefminingdad.autoplayer.blockmanager;

import com.chiefminingdad.autoplayer.node.NodeManager;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * [BlockManager] handles all getting and storing of [BlockState] data to find the best path.
 */
public class BlockManager{
    NodeManager nodeManager;

    Dictionary<Vec3i, BlockState> blockStateDictionary = new Hashtable<>();

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

    /**
     *
     * @return {@link Boolean} Whether there was a change in value of the block states.
     */
    public boolean getAllBlockStates(){
        AtomicBoolean changed = new AtomicBoolean(false);
        nodeManager.ReferenceTable.keys().asIterator().forEachRemaining(vec3i -> {
            BlockState previousBlockState = blockStateDictionary.get(vec3i);
            if(blockStateDictionary.put(vec3i, getBlockState(vec3i))!=null){
                changed.set(true);
            }
        });
        return changed.get();
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

                BlockState blockState = getBlockState(player.getOnPos(),level);

                commandContext.getSource().sendFeedback(Component.literal(blockState.toString()));
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }
    }

}
