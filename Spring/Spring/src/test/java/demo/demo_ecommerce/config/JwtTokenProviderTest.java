package demo.demo_ecommerce.config;


import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "wz9Y06/+4CQsy4hrYrajmK7480Qm6br0/mX4wk5n7WxCg/xzuV7ryQm17W+6pBbcwxSJbYk2iGInHPCMRuij2w==");
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", 3600000); // 1 ora
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        assertNotNull(token);
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);

        assertEquals("testuser", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void testValidateToken_Success() {
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateToken_Expired() {
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", -1); // Token già scaduto
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        assertFalse(jwtTokenProvider.validateToken(token));
    }
}

