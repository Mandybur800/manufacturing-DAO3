package com.manufacturing.web.filters;

import com.manufacturing.lib.Injector;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter implements Filter {
    private static final String DRIVER_ID = "driver_id";
    private static final Injector injector = Injector.getInstance("com.manufacturing");
    private final Set<String> allowedUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        allowedUrls.add("/login");
        allowedUrls.add("/drivers/create");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String servletPath = req.getServletPath();
        if (allowedUrls.contains(servletPath)) {
            chain.doFilter(req, resp);
            return;
        }
        if (req.getSession().getAttribute(DRIVER_ID) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
