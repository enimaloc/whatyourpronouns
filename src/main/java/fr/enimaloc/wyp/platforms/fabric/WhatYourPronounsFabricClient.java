package fr.enimaloc.wyp.platforms.fabric;

import fr.enimaloc.wyp.WYPCommands;
import fr.enimaloc.wyp.WhatYourPronounsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WhatYourPronounsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WhatYourPronounsClient.initClient();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(literal("wyp")
						.then(literal("refresh")
								.executes(context -> {
									context.getSource().sendFeedback(WYPCommands.refresh());
									return 1;
								}))));
	}
}
