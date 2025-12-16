package com.medilabo.notes.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
