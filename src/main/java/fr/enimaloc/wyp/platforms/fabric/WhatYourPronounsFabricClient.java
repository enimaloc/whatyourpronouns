package fr.enimaloc.wyp.platforms.fabric;

import fr.enimaloc.wyp.WYPCommands;
import fr.enimaloc.wyp.WhatYourPronounsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WhatYourPronounsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WhatYourPronounsClient.initClient();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> WYPCommands.onJoin());

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(literal("wyp")
						.then(literal("refresh")
								.executes(context -> {
									context.getSource().sendFeedback(WYPCommands.refresh());
									return 1;
								}))
						.then(literal("hint")
								.executes(context -> {
									WYPCommands.showNoPronounHint();
									return 1;
								})
								.then(literal("hide")
										.executes(context -> {
											context.getSource().sendFeedback(WYPCommands.hideNoPronounHint());
											return 1;
										})))));
	}
}
