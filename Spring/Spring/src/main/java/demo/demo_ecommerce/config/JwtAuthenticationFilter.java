package demo.demo_ecommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Rimuovi "Bearer " dal token
            try {
                // Recupera i Claims dal token
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);

                // Estrai il nome utente dal token
                username = claims.getSubject();

                // Puoi anche estrarre i ruoli o altre informazioni dai Claims
                String role = claims.get("role", String.class); // Ad esempio, ruolo
            } catch (ExpiredJwtException e) {
                System.out.println("Token expired: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.out.println("Unsupported token: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.out.println("Malformed token: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        // Se il nome utente è valido e l'utente non è già autenticato
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Crea un'istanza di autenticazione (puoi includere ruoli se presenti nel token)
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, null); // Aggiungi ruoli se disponibili
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Imposta l'autenticazione nel contesto di sicurezza
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Passa al prossimo filtro nella catena
        filterChain.doFilter(request, response);
    }
}
