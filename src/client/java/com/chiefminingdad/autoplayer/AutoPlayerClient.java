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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AutoPlayerClient implements ClientModInitializer {
    public static final BlockManager blockManager = new BlockManager();
	public static final CommandManager commandManager = new CommandManager();
	public static PathManager pathManager;
	public static Thread thread;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}

	/**
	 * Creates a new pathManager instance with the end location as, now the world should be loaded.
	 * Then it creates the Thread that runs the algorithm and starts it off.
	 * @param endLocation The location the algorithm will pathfind to.
	 */
	public static void setPathManager(Vec3i endLocation){
		pathManager = new PathManager(endLocation);
		pathManager.running = true;
		thread = new Thread(new runPathManager());
		thread.start();
	}

	/**
	 * Gets from the context of the command the Coordinates the User wants to pathfind and calls {@link setPathManager}
	 * with that location as the endLocation.
	 * @param commandContext The context passed on from the command move.
	 * @return Whether the code worked.
	 */
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

	/**
	 * Thread that runs the pathfinding algorithm separate from the main thread so it doesn't affect performance.
	 */
	private static class runPathManager extends Thread{
		public void run() {
			while(pathManager != null&&pathManager.Path.length==0) {
				if (pathManager.running) pathManager.run();
			}
			AutoPlayer.LOGGER.info("Ended Pathfinding");
		}
	}
}