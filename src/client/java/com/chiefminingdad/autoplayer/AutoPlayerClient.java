package com.chiefminingdad.autoplayer;

import com.chiefminingdad.autoplayer.blockmanager.BlockManager;
import com.chiefminingdad.autoplayer.commandmanager.CommandManager;
import com.chiefminingdad.autoplayer.pathmanager.PathManager;
import com.chiefminingdad.autoplayer.types.Coords;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;

public class AutoPlayerClient implements ClientModInitializer {
	public static final BlockManager blockManager = new BlockManager();
	public static final CommandManager commandManager = new CommandManager();
	public static PathManager pathManager;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

	}

	public static void setPathManager(Vec3i endLocation){
		pathManager = new PathManager(endLocation);
	}

	public static int setPathManagerWithCommand(CommandContext<FabricClientCommandSource> commandContext){
		try {
			Vec3i blockPos = commandContext.getArgument("Coordinates", Coords.class).getPositioni(commandContext.getSource());
			setPathManager(blockPos);
			commandContext.getSource().sendFeedback(Component.literal("Set End Location To "+blockPos.toShortString()));
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}
}