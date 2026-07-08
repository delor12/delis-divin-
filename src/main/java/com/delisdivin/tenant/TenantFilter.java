package com.delisdivin.tenant;

import com.delisdivin.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    private static final Pattern RESTAURANT_PATH_PATTERN = Pattern.compile("/(?:restaurant|api/restaurant)/(\\d+)");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            Long tenantId = null;

            // 1. Resolve from authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                if (userDetails.getRestaurantId() != null) {
                    tenantId = userDetails.getRestaurantId();
                    log.debug("Resolved tenant ID {} from logged-in user {}", tenantId, userDetails.getUsername());
                }
            }

            // 2. Resolve from Header X-Tenant-ID
            if (tenantId == null) {
                String tenantHeader = request.getHeader("X-Tenant-ID");
                if (tenantHeader != null && !tenantHeader.isBlank()) {
                    try {
                        tenantId = Long.parseLong(tenantHeader);
                        log.debug("Resolved tenant ID {} from X-Tenant-ID header", tenantId);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid X-Tenant-ID header value: {}", tenantHeader);
                    }
                }
            }

            // 3. Resolve from URL Path (e.g., /restaurant/{id}/** or /api/restaurant/{id}/**)
            if (tenantId == null) {
                String uri = request.getRequestURI();
                Matcher matcher = RESTAURANT_PATH_PATTERN.matcher(uri);
                if (matcher.find()) {
                    try {
                        tenantId = Long.parseLong(matcher.group(1));
                        log.debug("Resolved tenant ID {} from path URI: {}", tenantId, uri);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse tenant ID from path: {}", matcher.group(1));
                    }
                }
            }

            // Set context if resolved
            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
            } else {
                log.debug("No tenant context resolved for URI: {}", request.getRequestURI());
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clean up to prevent memory leaks in thread pool
            TenantContext.clear();
        }
    }
}
