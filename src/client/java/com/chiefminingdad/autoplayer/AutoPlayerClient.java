package com.chiefminingdad.autoplayer;

import com.chiefminingdad.autoplayer.blockmanager.BlockManager;
import com.chiefminingdad.autoplayer.commandmanager.CommandManager;
import net.fabricmc.api.ClientModInitializer;

public class AutoPlayerClient implements ClientModInitializer {
	public static final BlockManager blockManager = new BlockManager();
	public static final CommandManager commandManager = new CommandManager();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}