package demo.demo_ecommerce.config;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Genera una chiave sicura
        System.out.println("Secret Key: " + java.util.Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}
