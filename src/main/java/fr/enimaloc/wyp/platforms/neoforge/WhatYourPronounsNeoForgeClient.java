package fr.enimaloc.wyp.platforms.neoforge;

import fr.enimaloc.wyp.WhatYourPronouns;
import fr.enimaloc.wyp.WhatYourPronounsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = WhatYourPronouns.MOD_ID, dist = Dist.CLIENT)
public class WhatYourPronounsNeoForgeClient {
	public WhatYourPronounsNeoForgeClient(IEventBus modEventBus) {
		WhatYourPronounsClient.initClient();
	}
}
