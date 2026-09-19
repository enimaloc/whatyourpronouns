package fr.enimaloc.wyp.platforms.neoforge;

import fr.enimaloc.wyp.WhatYourPronouns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(WhatYourPronouns.MOD_ID)
public class WhatYourPronounsNeoForge {
	public WhatYourPronounsNeoForge(IEventBus modEventBus) {
		WhatYourPronouns.init();
	}
}
