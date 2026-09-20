package fr.enimaloc.wyp.platforms.neoforge;

import fr.enimaloc.wyp.WYPCommands;
import fr.enimaloc.wyp.WhatYourPronouns;
import fr.enimaloc.wyp.WhatYourPronounsClient;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = WhatYourPronouns.MOD_ID, dist = Dist.CLIENT)
public class WhatYourPronounsNeoForgeClient {
	public WhatYourPronounsNeoForgeClient(IEventBus modEventBus) {
		WhatYourPronounsClient.initClient();

		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> WYPCommands.onJoin());

		NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
				event.getDispatcher().register(Commands.literal("wyp")
						.then(Commands.literal("refresh")
								.executes(context -> {
									context.getSource().sendSuccess(WYPCommands::refresh, false);
									return 1;
								}))
						.then(Commands.literal("hint")
								.executes(context -> {
									WYPCommands.showNoPronounHint();
									return 1;
								})
								.then(Commands.literal("hide")
										.executes(context -> {
											context.getSource().sendSuccess(WYPCommands::hideNoPronounHint, false);
											return 1;
										})))));
	}
}
