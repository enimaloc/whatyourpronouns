package fr.enimaloc.wyp.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WYPCache {
    // PronounDB's "sets" values (see PronounAPI) are fixed English identifiers - "she", "he",
    // "they", "it", "any", "other", "ask", "avoid" - not player-facing text by themselves, so
    // they're routed through the mod's own translation keys instead of shown as-is.
    private static final String PRONOUN_KEY_PREFIX = "whatyourpronouns.pronoun.";
    private static final String LOADING_KEY = "whatyourpronouns.loading";

    // Language.getOrDefault() (which Component.getString() resolves through) returns the key
    // itself when no translation exists; fall back to `fallback` in that case instead of
    // showing the raw key. formatPronoun()/asComponent() rely on this never returning null.
    private static String translate(String key, String fallback) {
        String translated = Component.translatable(key).getString();
        return translated.equals(key) ? fallback : translated;
    }

    public record Entry(long invalidAt, Map<Locale, String[]> pronouns) {
        public String formatPronoun(Locale locale) {
            if (this == LOADING) {
                return translate(LOADING_KEY, "Loading...");
            }
            String[] pronoun = pronouns.getOrDefault(locale, new String[0]);
            String[] finalPronoun = new String[Math.min(pronoun.length, 2)];
            System.arraycopy(pronoun, 0, finalPronoun, 0, finalPronoun.length);
            for (int i = 0; i < finalPronoun.length; i++) {
                finalPronoun[i] = translate(PRONOUN_KEY_PREFIX + finalPronoun[i], finalPronoun[i]);
            }
            return switch (finalPronoun.length) {
                case 2 -> finalPronoun[0] + "/" + finalPronoun[1];
                case 1 -> finalPronoun[0];
                default -> ""; // length = 0
            };
        }

        public ChatFormatting color() {
            // Deliberately the raw (untranslated) code, not formatPronoun(): translated text
            // depends on the viewer's Minecraft language and would never match these literals.
            String[] codes = pronouns.getOrDefault(Locale.ENGLISH, new String[0]);
            if (codes.length == 0) return ChatFormatting.DARK_GRAY;
            return switch (codes[0]) {
                case "she" -> ChatFormatting.LIGHT_PURPLE;
                case "he" -> ChatFormatting.GREEN;
                case "they" -> ChatFormatting.AQUA;
                default -> ChatFormatting.DARK_GRAY;
            };
        }

        public Component asComponent(Locale locale, Component name) {
            String text = formatPronoun(locale);
            if (text.isBlank()) return name;
            return Component.empty()
                    .append(Component.literal(text).withStyle(color()))
                    .append(" ")
                    .append(name);
        }
    }

    public static final Entry INVALID = new Entry(0, Map.of());
    public static final Entry LOADING = new Entry(Long.MAX_VALUE, Map.of());
    // PronounDB API v2's bulk lookup endpoint accepts at most 50 ids per request; a request over
    // that cap is rejected outright, which would otherwise stall every pending lookup at once
    // (e.g. Enhanced Player List's offline-players panel queuing hundreds of uuids).
    private static final int MAX_BATCH_SIZE = 50;
    private static final Map<UUID, Entry> CACHE = new HashMap<>();
    private static final Map<UUID, CompletableFuture<Entry>> TO_FETCH = new HashMap<>();
    private static final Thread thread = new Thread(() -> {
        while (true) {
            if (!TO_FETCH.isEmpty()) {
                List<UUID> pending = new ArrayList<>(TO_FETCH.keySet());
                for (int i = 0; i < pending.size(); i += MAX_BATCH_SIZE) {
                    List<UUID> batch = pending.subList(i, Math.min(i + MAX_BATCH_SIZE, pending.size()));
                    List<PronounLookup> lookups = PronounAPI.fetch(batch.toArray(UUID[]::new));
                    for (PronounLookup lookup : lookups) {
                        CACHE.put(lookup.uuid(), new Entry(System.currentTimeMillis() + (1000 * 60 * 60 * 6), lookup.pronouns()));
                        TO_FETCH.remove(lookup.uuid()).complete(CACHE.get(lookup.uuid()));
                    }
                }
            }
            for (Map.Entry<UUID, Entry> entry : CACHE.entrySet()) {
                if (entry.getValue().invalidAt() < System.currentTimeMillis()) {
                    TO_FETCH.put(entry.getKey(), new CompletableFuture<>());
                }
            }
            try {
                Thread.sleep(30*1000);
            } catch (InterruptedException e) {}
        }
    });

    static {
        thread.start();
    }

    public static List<CompletableFuture<Entry>> get(UUID... uuids) {
        List<CompletableFuture<Entry>> entries = new ArrayList<>();
        for (UUID uuid : uuids) {
            Entry entry = CACHE.getOrDefault(uuid, INVALID);
            if (entry.invalidAt() == 0) {
                CompletableFuture<Entry> future = TO_FETCH.getOrDefault(uuid, new CompletableFuture<>());
                TO_FETCH.put(uuid, future);
                entries.add(future);
            }
            entries.add(CompletableFuture.completedFuture(entry));
        }
        fetchWaiting();
        return entries;
    }

    public static CompletableFuture<Entry> get(UUID uuid) {
        return get(new UUID[]{uuid}).get(0);
    }

    public static void evict(UUID... uuids) {
        for (UUID uuid : uuids) {
            CACHE.remove(uuid);
            CompletableFuture<Entry> pending = TO_FETCH.remove(uuid);
            if (pending != null) {
                pending.complete(INVALID);
            }
        }
    }

    /**
     * Evicts every cached and pending entry, forcing the next {@link #get} for each uuid to
     * re-fetch. Backs the "/wyp refresh" command; the actual re-fetch happens lazily, next time
     * something (chat, tab list, ...) asks for that uuid again.
     */
    public static int refreshAll() {
        Set<UUID> uuids = new HashSet<>(CACHE.keySet());
        uuids.addAll(TO_FETCH.keySet());
        evict(uuids.toArray(UUID[]::new));
        return uuids.size();
    }

    private static void fetchWaiting() {
    }
}
