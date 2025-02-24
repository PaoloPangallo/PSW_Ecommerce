package demo.demo_ecommerce.config;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username) // Imposta il nome utente come soggetto
                .claim("role", role) // Aggiungi il ruolo come claim
                .setIssuedAt(now) // Data di creazione
                .setExpiration(validity) // Data di scadenza
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()) // Firma il token
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
