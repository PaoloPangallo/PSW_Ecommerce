package demo.demo_ecommerce;

import demo.demo_ecommerce.entities.Role;
import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.UsersRepository;
import demo.demo_ecommerce.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsersServiceCachingTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("cacheduser_" + System.nanoTime());
        testUser.setEmail("cache_" + System.nanoTime() + "@example.com");
        testUser.setRole(Role.USER);
        testUser.setPassword("password");
        testUser = usersRepository.save(testUser);

        // Pulisci le cache
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void shouldCacheUserById() {
        Long id = testUser.getId();

        // Prima chiamata: DB
        User firstCall = usersService.getUserById(id);

        // Seconda chiamata: da cache
        User secondCall = usersService.getUserById(id);

        assertThat(firstCall).isEqualTo(secondCall);

        Cache cache = cacheManager.getCache("userById");
        assertThat(cache).isNotNull();
        assertThat(cache.get(id)).isNotNull();
    }

    @Test
    void shouldEvictUserFromCacheOnDelete() {
        Long id = testUser.getId();

        // Carica in cache
        usersService.getUserById(id);
        Cache cache = cacheManager.getCache("userById");
        assertThat(cache).isNotNull();
        assertThat(cache.get(id)).isNotNull();

        // Elimina utente (che dovrebbe anche evictare)
        usersService.deleteUser(id);
        assertThat(cache.get(id)).isNull();
    }

    @Test
    void shouldCacheUserByUsername() {
        String username = testUser.getUsername();

        Optional<User> first = usersService.findByUsername(username);
        Optional<User> second = usersService.findByUsername(username);

        assertThat(first).isPresent();
        assertThat(second).isPresent();
        assertThat(first.get()).isEqualTo(second.get());

        Cache cache = cacheManager.getCache("userByUsername");
        assertThat(cache).isNotNull();
        assertThat(cache.get(username)).isNotNull();
    }
}
