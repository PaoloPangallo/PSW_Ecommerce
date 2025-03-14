package demo.demo_ecommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    // Variabile per il caching della chiave segreta
    private SecretKey cachedKey;

    /**
     * Restituisce la chiave segreta decodificata, utilizzando il caching.
     */
    private SecretKey getKey() {
        if (cachedKey == null) {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(secretKey);
                cachedKey = Keys.hmacShaKeyFor(keyBytes);
            } catch (Exception e) {
                logger.error("Errore nella decodifica della chiave JWT: {}", e.getMessage());
                throw new RuntimeException("Chiave JWT non valida, verifica jwt.secret in application.properties");
            }
        }
        return cachedKey;
    }

    /**
     * Genera un token JWT per l'utente con il ruolo specificato.
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();

        logger.info("JWT generato correttamente per utente: {}", username);
        return token;
    }

    /**
     * Verifica se il token è valido.
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("Token ricevuto per validazione = [{}]", token);
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("Firma JWT non valida: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("Token JWT scaduto: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("Token JWT malformato: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.error("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Stringa del token JWT vuota: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Estrae le claims dal token JWT.
     */
    public Claims getClaimsFromToken(String token) {
        try {
            logger.debug("Token ricevuto per estrazione claims = [{}]", token);
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Errore nell'estrazione delle claims dal JWT: {}", e.getMessage());
            throw new RuntimeException("Token JWT non valido.");
        }
    }
}
