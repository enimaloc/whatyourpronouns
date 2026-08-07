package fr.enimaloc.wyp.api;

import com.llamalad7.mixinextras.sugar.Local;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record PronounLookup(UUID uuid, Map<Locale, String[]> pronouns) {
    public PronounLookup(UUID uuid) {
        this(uuid, new HashMap<>());
    }
}
