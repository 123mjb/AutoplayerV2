package com.chiefminingdad.autoplayer.commandmanager;


import com.chiefminingdad.autoplayer.AutoPlayerClient;
import com.chiefminingdad.autoplayer.blockmanager.BlockManager;
import com.chiefminingdad.autoplayer.types.arguments.BlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;

/**
 * Contains the needed logic for registering commands.
 */
public class CommandManager {
    public CommandManager(){
        registerTest();
        registerMove();
    }

    /**
     * Registers the "test2" command which has all the in game tests implemented to make sure features are working correctly.
     */
    private void registerTest(){
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) ->{
            dispatcher.register(ClientCommands.literal("test2")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Called /test with no arguments."));
                        return 1;
                    })
                    .then(ClientCommands.literal("getblock").executes(BlockManager.Tests::getBlock))
            );
        }));
    }

    /**
     * Registers the move command which is used to start and toggle the pathfinding.
     */
    private void registerMove(){
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) ->{
            dispatcher.register(ClientCommands.literal("move")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Toggled Pathfinding"));
                        AutoPlayerClient.pathManager.running = !AutoPlayerClient.pathManager.running;
                        return 1;
                    }).then(ClientCommands.argument("Coordinates", new BlockPosArgumentType()).executes(AutoPlayerClient::setPathManagerWithCommand))

            );
        }));
    }
}
