package com.medilabo.ui.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * InternalAuthFilter is a servlet filter that intercepts incoming HTTP requests to the application.
 * It logs the user's role from the "X-User-Role" header for tracking and debugging purposes.
 * The filter allows requests to static resources and actuator endpoints (like "/actuator", "/css", "/js", etc.) to pass through without logging.
 * For all other paths, it logs the user's role and continues the filter chain without performing authentication or authorization checks.
 */

@Component
public class InternalAuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(InternalAuthFilter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (path.startsWith("/actuator")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.startsWith("/webjars")) {
            chain.doFilter(request, response);
            return;
        }

        String role = req.getHeader("X-User-Role");
        log.info("UI InternalAuthFilter: X-User-Role='{}'", role);

        chain.doFilter(request, response);

    }
}
