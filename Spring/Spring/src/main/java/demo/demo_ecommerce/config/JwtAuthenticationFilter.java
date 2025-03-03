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

        // Salta il filtro per le richieste OPTIONS (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/cart")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Rimuove "Bearer "
            try {
                Claims claims = jwtTokenProvider.getClaimsFromToken(token);
                username = claims.getSubject();
                String role = claims.get("role", String.class);
                System.out.println("DEBUG JWT: username=" + username + ", role=" + role);
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("DEBUG JWT: Authentication impostata: " + authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                System.out.println("Token expired: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.out.println("Unsupported token: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.out.println("Malformed token: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG JWT: Header Authorization mancante o mal formato");
        }

        filterChain.doFilter(request, response);
        System.out.println("DEBUG JWT: SecurityContext dopo filtro: " + SecurityContextHolder.getContext().getAuthentication());
    }



}
