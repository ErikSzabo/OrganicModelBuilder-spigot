package me.devrik.organicmodelbuilder.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Placeholder {
    private final HashMap<String, String> placeholders = new HashMap<>();

    public Placeholder add(String key, String value) {
        placeholders.put(key, value);
        return this;
    }

    public Map<String, String> build() {
        return Collections.unmodifiableMap(placeholders);
    }

    public static Map<String, String> of(String key, String value) {
        return Collections.singletonMap(key, value);
    }
}
