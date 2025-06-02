package demo.demo_ecommerce.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats();
    }

    @Bean
    public CacheManager caffeineCacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "products", "product", "allProducts", "productsByCategory",
                "featuredProducts", "productsByCategoryPaged", "allProductsPaged",
                "productsPaged", "userById", "userByUsername", "userProfileSummary", "userReviews", "usersByRole", "couponsPaged"
        );
        manager.setCaffeine(caffeine);

        return manager;
    }

    // Questo metodo può essere richiamato manualmente (ad esempio da un test)
    public void logInitialStats(CacheManager cacheManager) {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache instanceof org.springframework.cache.caffeine.CaffeineCache caffeineCache) {
                CacheStats stats = caffeineCache.getNativeCache().stats();
                log.info("📦 Cache [{}] - Hits: {}, Misses: {}, Evictions: {}, Load Time: {} ns",
                        name,
                        stats.hitCount(),
                        stats.missCount(),
                        stats.evictionCount(),
                        stats.averageLoadPenalty());

            }
        }
    }
}
