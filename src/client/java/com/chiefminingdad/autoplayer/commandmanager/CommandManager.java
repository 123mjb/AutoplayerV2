package com.chiefminingdad.autoplayer.commandmanager;


import com.chiefminingdad.autoplayer.AutoPlayerClient;
import com.chiefminingdad.autoplayer.blockmanager.BlockManager;
import com.chiefminingdad.autoplayer.types.arguments.BlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;

public class CommandManager {
    public CommandManager(){
        registerTest();
        registerMove();
    }

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

    private void registerMove(){
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) ->{
            dispatcher.register(ClientCommands.literal("move")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Called move with no arguments."));
                        return 1;
                    }).then(ClientCommands.argument("Coordinates", new BlockPosArgumentType()).executes(AutoPlayerClient::setPathManagerWithCommand))

            );
        }));
    }
}
