package com.firewise.admin.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiKeyCacheService {

    private static final long TTL_SECONDS = 600; // 10 minutes

    private record CacheEntry(String apiKey, Instant expiresAt) {}

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public void storeKey(String sessionId, String apiKey) {
        cache.put(sessionId, new CacheEntry(apiKey, Instant.now().plusSeconds(TTL_SECONDS)));
    }

    public String getKey(String sessionId) {
        CacheEntry entry = cache.get(sessionId);
        if (entry == null) return null;
        if (Instant.now().isAfter(entry.expiresAt())) {
            cache.remove(sessionId);
            return null;
        }
        return entry.apiKey();
    }

    public boolean hasValidKey(String sessionId) {
        return getKey(sessionId) != null;
    }
}
