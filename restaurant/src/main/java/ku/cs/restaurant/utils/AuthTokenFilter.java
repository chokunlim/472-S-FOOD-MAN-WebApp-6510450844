package ku.cs.restaurant.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.*;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private static final Logger tokenLogger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // URI roles mapping
    private static final Map<String, Set<String>> uriRoleMap = new HashMap<>();

    static {
        // Define roles required for specific URI prefixes
        uriRoleMap.put("/order", new HashSet<>(Arrays.asList("RIDER", "COOK", "ADMIN", "CUSTOMER")));
        uriRoleMap.put("/recipe", new HashSet<>(Arrays.asList("RIDER", "COOK", "ADMIN")));
        uriRoleMap.put("/user", new HashSet<>(Arrays.asList("RIDER", "COOK", "ADMIN", "CUSTOMER")));
        uriRoleMap.put("/foods", new HashSet<>(Arrays.asList("RIDER", "CUSTOMER", "ADMIN", "COOK")));
        uriRoleMap.put("/order_line", new HashSet<>(Arrays.asList("RIDER", "CUSTOMER", "ADMIN", "COOK")));
        uriRoleMap.put("/receipt", new HashSet<>(Arrays.asList("RIDER", "CUSTOMER", "ADMIN", "COOK")));
        uriRoleMap.put("/financial", new HashSet<>(Collections.singletonList("ADMIN")));
        uriRoleMap.put("/review", new HashSet<>(Arrays.asList("ADMIN", "CUSTOMER")));
        uriRoleMap.put("/ingredient", new HashSet<>(Collections.singletonList("ADMIN")));
        uriRoleMap.put("/comment", new HashSet<>(Arrays.asList("ADMIN", "CUSTOMER")));
    }

    public AuthTokenFilter(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = parseJwt(request);

        // Allow public URIs (signin/signup, images, Swagger, etc.)
        if (isPublicUri(request.getRequestURI())) {
            filterChain.doFilter(request, response); // Proceed without JWT validation
            return;
        }

        // Validate JWT token
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            tokenLogger.warn("JWT is missing or invalid.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return; // Stop processing if the JWT is missing or invalid
        }

        try {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Extract roles from JWT claims
            List<String> roles = jwtUtils.getRolesFromToken(jwt);
            tokenLogger.info("User {} has roles: {}", username, roles);

            // Check if the user has the required role for the requested URI
            if (hasRequiredRoleForUri(request.getRequestURI(), roles)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Credentials are not required for JWT
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                tokenLogger.warn("User {} does not have the required role for this request.", username);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }
        } catch (Exception e) {
            tokenLogger.error("Cannot set user authentication: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Helper method to check if URI is public (no token required)
    private boolean isPublicUri(String requestUri) {
        return requestUri.equals("/auth/signin")
                || requestUri.equals("/auth/signup")
                || requestUri.equals("/auth")
                || requestUri.contains("/images")
                || requestUri.startsWith("/v3/api-docs")
                || requestUri.startsWith("/swagger-ui");
    }

    /**
     * Check if the user has the required role for the given URI
     * @param requestUri The requested URI
     * @param roles List of roles the user has
     * @return true if the user has the required role for the URI, false otherwise
     */
    private boolean hasRequiredRoleForUri(String requestUri, List<String> roles) {
        // Iterate over the URI role mappings and check the URI
        for (Map.Entry<String, Set<String>> entry : uriRoleMap.entrySet()) {
            String pathPrefix = entry.getKey();
            Set<String> requiredRoles = entry.getValue();

            // If the URI starts with the defined prefix, check if the roles match
            if (requestUri.startsWith(pathPrefix)) {
                for (String role : roles) {
                    if (requiredRoles.contains(role)) {
                        return true; // Role matches, allow access
                    }
                }
                return false; // No matching role, deny access
            }
        }

        return false; // Return false if URI doesn't match any condition
    }

    // Helper method to parse JWT from the Authorization header
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }
}
