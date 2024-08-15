package net.foodeals.authentication.infrastructure.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        try {
            if (!isJwtAuthRequest(authHeader)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = extractToken(authHeader);
            processJwtAuthRequest(request, token);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handleException(response, e);
        }

    }

    private void processJwtAuthRequest(final HttpServletRequest request, final String token) {
//        final String userEmail = userDetailsService.loadUserByUsername()
    }

    private String extractToken(String authHeader) {
        return authHeader.substring(7);
    }

    private boolean isJwtAuthRequest(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    private void handleException(final HttpServletResponse response, final Exception e) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String jsonError = "{\"error\": \"" + e.getMessage() + "\"}";
            response.getWriter().write(jsonError);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
