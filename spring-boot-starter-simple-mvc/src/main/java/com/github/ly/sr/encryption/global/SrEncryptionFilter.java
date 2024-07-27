package com.github.ly.sr.encryption.global;

import com.github.ly.enums.EncryptMode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class SrEncryptionFilter implements Filter {
    private final Set<String> skipPaths;
    private final EncryptMode encryptMode;
    private final String privateKey;

    public SrEncryptionFilter(final Set<String> skipPaths, final EncryptMode encryptMode, final String privateKey) {
        this.skipPaths = Collections.unmodifiableSet(skipPaths);
        this.encryptMode = encryptMode;
        this.privateKey = privateKey;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest request) {
            String servletPath = request.getServletPath();
            boolean skip = false;
            for (String skipPath : skipPaths) {
                if (servletPath.contains(skipPath)) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            if (request.getMethod().equalsIgnoreCase("post")) {
                SrHttpRequestWrapper requestWrapper = null;
                try {
                    requestWrapper = new SrHttpRequestWrapper(request, encryptMode, privateKey);
                } catch (Exception e) {
                    request.getRequestDispatcher("404.html").forward(request, servletResponse);
                }
                filterChain.doFilter(requestWrapper, servletResponse);
            } else if (request.getMethod().equalsIgnoreCase("options")) {
                log.info("收到 potions请求");
            } else { //其余默认get请求
                SrParameterRequestWrapper requestWrapper = null;
                try {
                    requestWrapper = new SrParameterRequestWrapper(request, encryptMode, privateKey);
                } catch (Exception e) {
                    request.getRequestDispatcher("404.html").forward(request, servletResponse);
                }
                filterChain.doFilter(requestWrapper, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
