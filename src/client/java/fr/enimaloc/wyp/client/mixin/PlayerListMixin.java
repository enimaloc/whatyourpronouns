package fr.enimaloc.wyp.client.mixin;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.CommonColors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerListMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract List<PlayerInfo> getPlayerInfos();

    @Inject(method = "renderPingIcon", at = @At("TAIL"))
    public void afterRenderPlayer(GuiGraphics context, int width, int x, int y, PlayerInfo entry, CallbackInfo ci) {
        CompletableFuture<WYPCache.Entry> cached = WYPCache.get(entry.getProfile().getId());
        String text = cached.getNow(WYPCache.LOADING).formatPronoun(Locale.ENGLISH);
        int textX = x + (width  - 12 - minecraft.font.width(text));
        context.drawString(minecraft.font, text, textX, y, CommonColors.GRAY, true);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int redirectPlayerListEntryWidth(int a, int b) {
        List<PlayerInfo> entries = getPlayerInfos();
        UUID[] uuids = entries
                .stream()
                .map(ent -> ent.getProfile().getId())
                .toArray(UUID[]::new);

        int longestPronoun = WYPCache.get(uuids).stream()
                .map(future -> future.getNow(WYPCache.LOADING))
                .map(entry -> entry.formatPronoun(Locale.ENGLISH))
                .mapToInt(minecraft.font::width)
                .max()
                .orElse(0);

        return Math.min(a + 5 + longestPronoun, b);
    }
}
