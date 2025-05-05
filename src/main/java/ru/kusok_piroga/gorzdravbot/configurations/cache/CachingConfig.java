package ru.kusok_piroga.gorzdravbot.configurations.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CachingConfig implements CachingConfigurer {

    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("districts",
                buildCache("districts", 10, 20, 10));
        cacheManager.registerCustomCache("polyclinics",
                buildCache("polyclinics",10, 500, 72));
        cacheManager.registerCustomCache("specialties",
                buildCache("specialties",10, 100, 72));
        cacheManager.registerCustomCache("doctors",
                buildCache("doctors",10, 500, 24));
        return cacheManager;
    }

    private Cache<Object, Object> buildCache(String cacheName, int initialCapacity, int maximumSize, int durationInHours) {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(durationInHours, TimeUnit.HOURS)
                .evictionListener((Object key, Object value, RemovalCause cause) ->
                        log.info("Cache {} was evicted ({})", cacheName, cause))
                .removalListener((Object key, Object value, RemovalCause cause) ->
                        log.info("Cache {} was removed ({})", cacheName, cause))
                .build();
    }
}