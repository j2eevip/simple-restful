package com.github.ly.sr.debounce;

import java.util.concurrent.TimeUnit;

public interface CacheSupport {
    boolean hasCacheKey(String key);

    void setCacheKey(String key, String value, long expire, TimeUnit timeUnit);

    void removeCacheKey(String key);
}
