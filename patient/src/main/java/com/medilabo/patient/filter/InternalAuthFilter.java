package com.medilabo.patient.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * InternalAuthFilter is a servlet filter that checks for a specific header ("X-Internal-Secret") in incoming HTTP requests to authenticate internal requests.
 * If the header value matches the expected secret, the request is allowed to proceed; otherwise, an unauthorized error is returned.
 * The filter bypasses authentication for requests to the "/actuator" endpoint.
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

        log.info("InternalAuthFilter: path={} X-Internal-Secret='{}' expected='{}'", path, header, internalSecret);

        if (internalSecret != null && internalSecret.equals(header)) {
            chain.doFilter(request, response);
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
        }
    }
}
