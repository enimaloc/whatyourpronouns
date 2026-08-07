package fr.enimaloc.wyp.client.mixin;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;
import java.util.UUID;

@Mixin(PlayerEntry.class)
public class SocialInteractionMixin {
    @Shadow @Final private UUID id;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int replaceText(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow) {
        WYPCache.Entry entry = WYPCache.get(id).getNow(WYPCache.LOADING);
        return instance.drawString(font, entry.asComponent(Locale.ENGLISH, Component.literal(text)), x, y, color);
    }
}
