package demo.demo_ecommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
        System.out.println("DEBUG JWT: Header Authorization ricevuto = [" + authHeader + "]");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // ✅ Rimuove SOLO la PRIMA occorrenza di "Bearer "
            String token = authHeader.replaceFirst("Bearer ", "").trim();

            // ✅ Se c'è ancora "Bearer", rimuovilo completamente
            if (token.startsWith("Bearer ")) {
                token = token.replaceFirst("Bearer ", "").trim();
            }

            System.out.println("DEBUG JWT: Token effettivo = [" + token + "]");

            try {
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                System.out.println("DEBUG JWT: username=" + username + ", role=" + role);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("DEBUG JWT: Authentication impostata: " + authenticationToken);
                }
            } catch (Exception e) {
                System.out.println("DEBUG JWT: Errore nella validazione del token - " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG JWT: Header Authorization mancante o mal formato");
        }

        filterChain.doFilter(request, response);
        System.out.println("DEBUG JWT: SecurityContext dopo filtro: " + SecurityContextHolder.getContext().getAuthentication());
    }



}
