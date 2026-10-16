package com.chiefminingdad.autoplayer;

import com.chiefminingdad.autoplayer.pathmanager.PathManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import net.fabricmc.api.*;
//import net.fabricmc.fabric.api.client.rendering.v1.*;
//import net.minecraft.client.*;
//import net.minecraft.client.gui.*;
//import net.minecraft.world.*;

public class AutoPlayerClient implements ClientModInitializer {
	public static final String MOD_ID = "autoplayer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	PathManager pathManager;


	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext) -> {
			commandDispatcher.register(ClientCommands.literal("move").executes(commandContext -> {
				commandContext.getSource().sendFeedback(Component.literal("Called move with no arguments"));

				return 1;
			}));
		}));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}