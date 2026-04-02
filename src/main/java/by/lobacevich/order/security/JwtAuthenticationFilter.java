package by.lobacevich.order.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_ROLE = "X-Role";

    private final JwtAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userIdString = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_ROLE);
        if (userIdString == null || role == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            UserPrincipal principal = new UserPrincipal(Long.parseLong(userIdString));
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    ));

            filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response,
                    new AuthenticationCredentialsNotFoundException("Invalid X-User-Id header", e));
        }
    }
}
