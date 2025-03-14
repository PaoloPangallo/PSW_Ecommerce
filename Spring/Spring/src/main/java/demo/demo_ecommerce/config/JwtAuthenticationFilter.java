package demo.demo_ecommerce.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.debug("Header Authorization ricevuto = [{}]", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Rimuove la stringa "Bearer " (eventuali duplicati se presenti)
            String token = authHeader.replaceFirst("Bearer ", "").trim();
            if (token.startsWith("Bearer ")) {
                token = token.replaceFirst("Bearer ", "").trim();
            }
            logger.debug("Token effettivo = [{}]", token);

            Claims claims;
            try {
                // Verifica ed estrazione delle claims (se invalido, lancia un'eccezione)
                claims = jwtTokenProvider.getClaimsFromToken(token);
            } catch (Exception e) {
                logger.error("Errore nella validazione del token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token non valido");
                return; // Interrompe la catena in caso di token non valido
            }

            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            logger.debug("username={}, role={}", username, role);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("Authentication impostata: {}", authenticationToken);
            }
        } else {
            logger.debug("Header Authorization mancante o mal formato");
        }

        filterChain.doFilter(request, response);
        logger.debug("SecurityContext dopo filtro: {}", SecurityContextHolder.getContext().getAuthentication());
    }
}
