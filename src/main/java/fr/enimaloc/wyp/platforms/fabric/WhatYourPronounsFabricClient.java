package fr.enimaloc.wyp.platforms.fabric;

import fr.enimaloc.wyp.WhatYourPronounsClient;
import net.fabricmc.api.ClientModInitializer;

public class WhatYourPronounsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WhatYourPronounsClient.initClient();
	}
}
