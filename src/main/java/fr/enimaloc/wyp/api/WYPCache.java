package fr.enimaloc.wyp.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WYPCache {
    public record Entry(long invalidAt, Map<Locale, String[]> pronouns) {
        public String formatPronoun(Locale locale) {
            String[] pronoun = pronouns.getOrDefault(locale, new String[0]);
            String[] finalPronoun = new String[Math.min(pronoun.length, 2)];
            System.arraycopy(pronoun, 0, finalPronoun, 0, finalPronoun.length);
            return switch (finalPronoun.length) {
                case 2 -> finalPronoun[0] + "/" + finalPronoun[1];
                case 1 -> finalPronoun[0];
                default -> ""; // length = 0
            };
        }

        public ChatFormatting color() {
            String text = formatPronoun(Locale.ENGLISH);
            if (text.isBlank()) return ChatFormatting.DARK_GRAY;
            text = text.split("/")[0];
            return switch (text) {
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
    public static final Entry LOADING = new Entry(Long.MAX_VALUE, Map.of(Locale.ENGLISH, new String[]{"Loading..."}));
    private static final Map<UUID, Entry> CACHE = new HashMap<>();
    private static final Map<UUID, CompletableFuture<Entry>> TO_FETCH = new HashMap<>();
    private static final Thread thread = new Thread(() -> {
        while (true) {
            if (!TO_FETCH.isEmpty()) {
                List<PronounLookup> lookups = PronounAPI.fetch(TO_FETCH.keySet().toArray(UUID[]::new));
                for (PronounLookup lookup : lookups) {
                    CACHE.put(lookup.uuid(), new Entry(System.currentTimeMillis() + (1000 * 60 * 60 * 6), lookup.pronouns()));
                    TO_FETCH.remove(lookup.uuid()).complete(CACHE.get(lookup.uuid()));
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

    private static void fetchWaiting() {
    }
}
