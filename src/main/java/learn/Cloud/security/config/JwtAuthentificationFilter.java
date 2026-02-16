package learn.Cloud.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learn.Cloud.security.JwtAuthenticationToken;
import learn.Cloud.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthentificationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Пытаемся найти JWT токен
        final String authHeader = request.getHeader("Authorization");

        // Если есть токен - аутентифицируем
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                final String jwt = authHeader.substring(7);
                final String userEmail = jwtService.extractUsername(jwt);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        JwtAuthenticationToken authToken = new JwtAuthenticationToken(
                                userDetails,
                                jwt,
                                userDetails.getAuthorities(),
                                jwtService.extractAllClaims(jwt)
                        );
                        authToken.setAuthenticated(true);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Пользователь {} аутентифицирован по JWT", userEmail);
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка при аутентификации JWT: {}", e.getMessage());
                // Не блокируем запрос, просто продолжаем без аутентификации
            }
        }

        // Всегда пропускаем запрос дальше
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Фильтр применяется ко всем запросам, но не блокирует их
        return false;
    }
}