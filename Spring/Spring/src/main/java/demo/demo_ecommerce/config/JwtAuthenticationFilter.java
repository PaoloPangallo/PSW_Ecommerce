package demo.demo_ecommerce.config;

import demo.demo_ecommerce.entities.User;
import demo.demo_ecommerce.repositories.UsersRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.debug("Header Authorization ricevuto = [{}]", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Rimuove il prefisso "Bearer "
            String token = authHeader.replaceFirst("Bearer ", "").trim();
            if (token.startsWith("Bearer ")) {
                token = token.replaceFirst("Bearer ", "").trim();
            }
            logger.debug("Token effettivo = [{}]", token);

            Claims claims;
            try {
                claims = jwtTokenProvider.getClaimsFromToken(token);
            } catch (Exception e) {
                logger.error("Errore nella validazione del token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token non valido");
                return;
            }

            String username = claims.getSubject();
            logger.debug("Username estratto dal token = [{}]", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = usersRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Utente non trovato nel database: " + username));

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                user, // ✅ principal è l'intero oggetto User
                                null,
                                user.getAuthorities()
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("Utente autenticato: [{}], ID: {}", user.getUsername(), user.getId());
            }
        } else {
            logger.debug("Header Authorization mancante o mal formato");
        }

        filterChain.doFilter(request, response);
    }
}
