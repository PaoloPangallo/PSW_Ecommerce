package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.services.CacheMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminCacheMetricsController {

    private final CacheMetricsService cacheMetricsService;

    public AdminCacheMetricsController(CacheMetricsService cacheMetricsService) {
        this.cacheMetricsService = cacheMetricsService;
    }

    @GetMapping("/admin/cache-metrics")
    public ResponseEntity<Map<String, Map<String, Object>>> getCacheStats() {
        return ResponseEntity.ok(cacheMetricsService.getAllStats());
    }

}
