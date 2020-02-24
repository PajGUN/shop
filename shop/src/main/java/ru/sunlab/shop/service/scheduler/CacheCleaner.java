package ru.sunlab.shop.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheCleaner {

    private static final String CACHE_NAME = "stores";

    private final CacheManager cacheManager;
    @Autowired
    public CacheCleaner(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "${cron.clean.cache.period}")
    public void cleanCache(){
        log.debug("cache cleaned");
        cacheManager.getCache(CACHE_NAME).clear();
    }
}
