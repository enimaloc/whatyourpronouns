package fr.enimaloc.wyp.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.enimaloc.wyp.WhatYourPronouns;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class PronounAPI {
    private static final String API_ROOT = "https://pronoundb.org/api/v2/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static List<PronounLookup> fetch(UUID... uuids) {
        WhatYourPronouns.LOGGER.info("Fetching pronouns for {}", uuids);
        URI uri;
        try {
            uri = new URIBuilder(API_ROOT + "lookup")
                    .addParameter("platform", "minecraft")
                    .addParameter("ids", String.join(",", Arrays.stream(uuids).map(UUID::toString).toArray(String[]::new)))
                    .build();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        try {
            HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                return List.of();
            }

            JsonObject json = JsonParser.parseReader(new InputStreamReader(response.body())).getAsJsonObject();

            List<PronounLookup> lookups = new ArrayList<>();

            for (UUID uuid : uuids) {
                if (!json.has(uuid.toString())) {
                    lookups.add(new PronounLookup(uuid));
                    continue;
                }

                Map<Locale, String[]> pronouns = new HashMap<>();
                JsonObject sets = json.getAsJsonObject(uuid.toString()).getAsJsonObject("sets");
                for (String locale : sets.keySet()) {
                    pronouns.put(Locale.forLanguageTag(locale), sets.get(locale)
                            .getAsJsonArray().asList()
                            .stream().map(JsonElement::getAsString).toArray(String[]::new));
                }
                lookups.add(new PronounLookup(uuid, pronouns));
            }
            return lookups;
        } catch (IOException | InterruptedException ignored) {
            return List.of();
        }
    }
}
