package demo.demo_ecommerce;


import demo.demo_ecommerce.config.CacheConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheConfig cacheConfig;

    @Test
    void allExpectedCachesShouldBePresent() {
        assertThat(cacheManager.getCache("products")).isNotNull();
        assertThat(cacheManager.getCache("product")).isNotNull();
        assertThat(cacheManager.getCache("allProducts")).isNotNull();
        assertThat(cacheManager.getCache("productsByCategory")).isNotNull();
        assertThat(cacheManager.getCache("featuredProducts")).isNotNull();
        assertThat(cacheManager.getCache("productsByCategoryPaged")).isNotNull();
        assertThat(cacheManager.getCache("allProductsPaged")).isNotNull();
        assertThat(cacheManager.getCache("productsPaged")).isNotNull();
        assertThat(cacheManager.getCache("userById")).isNotNull();
        assertThat(cacheManager.getCache("userByUsername")).isNotNull();
        assertThat(cacheManager.getCache("userProfileSummary")).isNotNull();
        assertThat(cacheManager.getCache("userReviews")).isNotNull();
        assertThat(cacheManager.getCache("usersByRole")).isNotNull();
    }

    @Test
    void shouldLogCacheStatisticsWithoutError() {
        // Questo test verifica che il metodo non lanci eccezioni
        cacheConfig.logInitialStats(cacheManager);
    }
}

