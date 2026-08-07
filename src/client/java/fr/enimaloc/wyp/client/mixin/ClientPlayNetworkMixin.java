package fr.enimaloc.wyp.client.mixin;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkMixin {
    @Inject(method = "getOnlinePlayers", at = @At("HEAD"))
    private void onPlayerInfo(CallbackInfoReturnable<Collection<PlayerInfo>> cir) {
        UUID[] uuids = Minecraft.getInstance().getConnection().getOnlinePlayerIds().toArray(UUID[]::new);
        WYPCache.get(uuids);
    }
}
