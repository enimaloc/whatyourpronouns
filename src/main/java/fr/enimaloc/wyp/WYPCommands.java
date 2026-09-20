package fr.enimaloc.wyp;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.network.chat.Component;

/**
 * Command logic shared between the Fabric and NeoForge client command registrations
 * (platforms/fabric and platforms/neoforge respectively use different Brigadier wiring APIs).
 */
public final class WYPCommands {
    private WYPCommands() {
    }

    public static Component refresh() {
        int refreshed = WYPCache.refreshAll();
        return Component.translatable("whatyourpronouns.command.refresh", refreshed);
    }
}
