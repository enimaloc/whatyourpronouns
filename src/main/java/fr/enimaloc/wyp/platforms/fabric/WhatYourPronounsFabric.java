package fr.enimaloc.wyp.platforms.fabric;

import fr.enimaloc.wyp.WhatYourPronouns;
import net.fabricmc.api.ModInitializer;

public class WhatYourPronounsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		WhatYourPronouns.init();
	}
}
