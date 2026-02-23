package com.medilabo.notes.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests to perform internal authentication based on a secret header and user role.
 * This filter checks for the presence of the "X-Internal-Secret" header and validates it against a configured secret value.
 * It also checks for the "X-User-Role" header to ensure that the user has the appropriate role (PRATICIEN) to access the resources.
 * If the authentication fails, the filter responds with an HTTP 401 Unauthorized status.
 */
@Component
public class InternalAuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(InternalAuthFilter.class);

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }
        String header = req.getHeader("X-Internal-Secret");
        log.info("UI InternalAuthFilter: path={} X-Internal-Secret='{}' expected='{}'", path, header, internalSecret);
        String role = req.getHeader("X-User-Role");
        log.info("UI InternalAuthFilter: X-User-Role='{}'", role);

        if (internalSecret != null && internalSecret.equals(header) && "PRATICIEN".equals(role)) {
            chain.doFilter(request, response);
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
        }
    }
}
