package fr.enimaloc.wyp.platforms.neoforge.compat.enhancedplayerlist;

import com.enhancedplayerlist.client.event.ClientEventHandler;
import com.enhancedplayerlist.data.PlayerStatsData;
import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.CommonColors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Mixin(ClientEventHandler.class)
public class EnhancedPlayerListRowMixin {
    @Shadow @Final private static int NAME_COLUMN_WIDTH;
    @Shadow @Final private static int PADDING;

    @Inject(
            method = "renderPlayerRows",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private static void wyp$renderPronoun(
            GuiGraphics graphics, List<PlayerStatsData> players, int startX, int startY,
            Map<String, Integer> columnWidths, List<String> statColumns, CallbackInfo ci,
            int rowY, Iterator<PlayerStatsData> playerIterator, PlayerStatsData playerData, int nameColor
    ) {
        if (!playerData.isOnline()) return;

        UUID uuid = UUID.fromString(playerData.getUuid());
        String text = WYPCache.get(uuid).getNow(WYPCache.LOADING).formatPronoun(Locale.ENGLISH);
        if (text.isBlank()) return;

        Minecraft minecraft = Minecraft.getInstance();
        int textX = startX + NAME_COLUMN_WIDTH - PADDING - minecraft.font.width(text);
        graphics.drawString(minecraft.font, text, textX, rowY, CommonColors.GRAY, true);
    }
}
