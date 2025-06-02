package demo.demo_ecommerce.services;


import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CacheMetricsService {

    private final CacheManager cacheManager;

    public CacheMetricsService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Map<String, Map<String, Object>> getAllStats() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (String name : cacheManager.getCacheNames()) {
            Cache springCache = cacheManager.getCache(name);
            if (springCache instanceof CaffeineCache caffeineCache) {
                CacheStats stats = caffeineCache.getNativeCache().stats();

                Map<String, Object> statMap = new HashMap<>();
                statMap.put("hitCount", stats.hitCount());
                statMap.put("missCount", stats.missCount());
                statMap.put("loadSuccessCount", stats.loadSuccessCount());
                statMap.put("loadFailureCount", stats.loadFailureCount());
                statMap.put("totalLoadTime", stats.totalLoadTime());
                statMap.put("evictionCount", stats.evictionCount());
                statMap.put("evictionWeight", stats.evictionWeight());

                result.put(name, statMap);
            }
        }
        return result;
    }


}

