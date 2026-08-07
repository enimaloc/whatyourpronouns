package fr.enimaloc.wyp.client.mixin;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPacketListener.class)
public class PlayerInfoRemoveMixin {
    @Inject(method = "handlePlayerInfoRemove", at = @At("HEAD"))
    private void onPlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        WYPCache.evict(packet.profileIds().toArray(UUID[]::new));
    }
}