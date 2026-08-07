package fr.enimaloc.wyp.client.mixin;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @ModifyVariable(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getFont()Lnet/minecraft/client/gui/Font;"), argsOnly = true)
    private Component modifyNametag(Component text, T entity) {
        if (!(entity instanceof Player player)) return text;

        UUID id = player.getGameProfile().getId();
        CompletableFuture<WYPCache.Entry> future = WYPCache.get(id);
        return future.getNow(WYPCache.LOADING).asComponent(Locale.ENGLISH, text);
    }
}
