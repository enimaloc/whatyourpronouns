package fr.enimaloc.wyp.client.mixin;

import com.mojang.authlib.GameProfile;
import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(ChatListener.class)
public abstract class ChatMessageMixin {
    @Shadow protected abstract boolean showMessageToPlayer(ChatType.Bound boundChatType, PlayerChatMessage chatMessage, Component decoratedServerContent, GameProfile gameProfile, boolean onlyShowSecureChat, Instant timestamp);

    @ModifyVariable(method = "showMessageToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/ChatTrustLevel;createTag(Lnet/minecraft/network/chat/PlayerChatMessage;)Lnet/minecraft/client/GuiMessageTag;"), argsOnly = true)
    private Component decorate(Component decorated, ChatType.Bound params, PlayerChatMessage message, Component dontuse, GameProfile senderEntry) {
        UUID id = senderEntry.getId();
        CompletableFuture<WYPCache.Entry> future = WYPCache.get(id);
        return future.getNow(WYPCache.LOADING).asComponent(Locale.ENGLISH, decorated);
    }
}
