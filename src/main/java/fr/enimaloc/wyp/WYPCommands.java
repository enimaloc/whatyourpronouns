package fr.enimaloc.wyp;

import fr.enimaloc.wyp.api.WYPCache;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.Locale;
import java.util.UUID;

/**
 * Command logic shared between the Fabric and NeoForge client command registrations
 * (platforms/fabric and platforms/neoforge respectively use different Brigadier wiring APIs).
 */
public final class WYPCommands {
    private static final String PRONOUNDB_URL = "https://pronoundb.org/";

    private WYPCommands() {
    }

    public static Component refresh() {
        int refreshed = WYPCache.refreshAll();
        return Component.translatable("whatyourpronouns.command.refresh", refreshed);
    }

    /**
     * Checked once per server join (see the platform Client classes): if the local player has no
     * pronoun set on PronounDB and hasn't hidden the reminder, nudges them towards setting one.
     */
    public static void onJoin() {
        if (WYPConfig.get().isNoPronounHintHidden()) return;

        UUID uuid = Minecraft.getInstance().getUser().getProfileId();
        WYPCache.get(uuid).thenAccept(entry -> {
            if (entry.pronouns().getOrDefault(Locale.ENGLISH, new String[0]).length > 0) return;
            Minecraft.getInstance().execute(WYPCommands::showNoPronounHint);
        });
    }

    /** "/wyp hint": shows the same reminder on demand, regardless of the hidden setting. */
    public static Component showNoPronounHint() {
        var player = Minecraft.getInstance().player;
        Component message = noPronounHintMessage();
        if (player != null) player.displayClientMessage(message, false);
        return message;
    }

    /** "/wyp hint hide": suppresses the automatic on-join reminder from now on. */
    public static Component hideNoPronounHint() {
        WYPConfig.get().hideNoPronounHint();
        return Component.translatable("whatyourpronouns.command.hint_hidden");
    }

    private static Component noPronounHintMessage() {
        Component link = Component.literal("PronounDB")
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.AQUA)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, PRONOUNDB_URL)));
        return Component.translatable("whatyourpronouns.hint.no_pronoun", link);
    }
}
