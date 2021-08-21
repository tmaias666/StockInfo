package com.ty.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig{

    @Bean
    public Cache<String, Object> caffeineCache(){
        return Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(20)
            .expireAfterAccess(1, TimeUnit.DAYS).recordStats().build();
    }
    
    //    @Bean
    //    public CaffeineCacheManager cacheManager(){
    //        CaffeineCacheManager cacheManager = new CaffeineCacheManager("stock");
    //        cacheManager.setCaffeine(caffeineCacheBuilder());
    //        return cacheManager;
    //    }
    //
    //    Caffeine<Object, Object> caffeineCacheBuilder(){
    //        return Caffeine.newBuilder()
    //            .initialCapacity(100)
    //            .maximumSize(20)
    //            .expireAfterAccess(1, TimeUnit.DAYS).recordStats();
    //    }
}
