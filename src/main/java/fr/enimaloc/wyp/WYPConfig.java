package fr.enimaloc.wyp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small client-side settings file at {@code config/whatyourpronouns.json}, read through the
 * vanilla game directory rather than a loader-specific config API so this class stays shared
 * between the Fabric and NeoForge nodes.
 */
public final class WYPConfig {
    private static WYPConfig instance;

    private boolean noPronounHintHidden;

    public static synchronized WYPConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public boolean isNoPronounHintHidden() {
        return noPronounHintHidden;
    }

    public void hideNoPronounHint() {
        if (noPronounHintHidden) return;
        noPronounHintHidden = true;
        save();
    }

    private static Path path() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(WhatYourPronouns.MOD_ID + ".json");
    }

    private static WYPConfig load() {
        Path path = path();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                WYPConfig loaded = new Gson().fromJson(reader, WYPConfig.class);
                if (loaded != null) return loaded;
            } catch (IOException | RuntimeException e) {
                WhatYourPronouns.LOGGER.warn("Failed to read {}, using defaults", path, e);
            }
        }
        return new WYPConfig();
    }

    private void save() {
        Path path = path();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
            }
        } catch (IOException e) {
            WhatYourPronouns.LOGGER.warn("Failed to write {}", path, e);
        }
    }
}
