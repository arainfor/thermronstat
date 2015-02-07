package com.arainfor.thermronstat;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ARAINFOR on 1/31/2015.
 */
public class StatusRelayCache {

    private static final Map<RelayMap, Boolean> cache = new LinkedHashMap<RelayMap, Boolean>();
    private static StatusRelayCache instance;

    public static synchronized StatusRelayCache getInstance() {
        if (instance == null) {
            instance = new StatusRelayCache();
        }
        return instance;
    }

    public void setValue(RelayMap relay, boolean value) {
        cache.put(relay, value);
    }

    public boolean getValue(RelayMap relay) {
        return cache.get(relay);
    }

    public Map<RelayMap, Boolean> getCache() {
        return cache;
    }
}
